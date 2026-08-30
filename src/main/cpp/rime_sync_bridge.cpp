#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>

#define LOG_TAG "YuyanRimeSync"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

typedef int (*RimeSyncUserDataFn)();
typedef void (*RimeJoinMaintenanceThreadFn)();
typedef void (*RimeCleanupAllSessionsFn)();

/**
 * 薄 JNI bridge：调用 libyuyanime.so 内已经存在的同一个 librime 实例。
 * 绝不在 APK 中再复制一份 librime，否则会形成两个实例同时打开同一 userdb。
 *
 * 语义：
 *   1. 清理现有 session（避免 userdb lock）
 *   2. sync_user_data()
 *   3. join_maintenance_thread()
 * 方法返回前完成全部工作，Java/Kotlin 层不做 polling/sleep。
 */
extern "C" JNIEXPORT jboolean JNICALL
Java_com_yuyan_inputmethod_core_Rime_syncRimeUserDataBlocking(
        JNIEnv* /*env*/, jclass /*clazz*/) {
    void* handle = dlopen("libyuyanime.so", RTLD_NOW | RTLD_LOCAL);
    if (!handle) {
        LOGE("dlopen(libyuyanime.so) failed: %s", dlerror());
        return JNI_FALSE;
    }

    // 4 个 ABI 均导出 rime_get_api、RimeSyncUserData、RimeJoinMaintenanceThread。
    // 该库中符号为 C++ mangled 名，先按 mangled 名解析，再兼容 unmangled 名。
    auto cleanup = reinterpret_cast<RimeCleanupAllSessionsFn>(
            dlsym(handle, "_Z22RimeCleanupAllSessionsv"));
    auto sync = reinterpret_cast<RimeSyncUserDataFn>(
            dlsym(handle, "_Z16RimeSyncUserDatav"));
    auto join = reinterpret_cast<RimeJoinMaintenanceThreadFn>(
            dlsym(handle, "_Z25RimeJoinMaintenanceThreadv"));

    if (!sync) {
        sync = reinterpret_cast<RimeSyncUserDataFn>(
                dlsym(handle, "RimeSyncUserData"));
    }
    if (!cleanup) {
        cleanup = reinterpret_cast<RimeCleanupAllSessionsFn>(
                dlsym(handle, "RimeCleanupAllSessions"));
    }
    if (!join) {
        join = reinterpret_cast<RimeJoinMaintenanceThreadFn>(
                dlsym(handle, "RimeJoinMaintenanceThread"));
    }

    if (!sync) {
        LOGE("RimeSyncUserData not exported by libyuyanime.so");
        return JNI_FALSE;
    }

    if (cleanup) {
        cleanup();
    }

    int ok = sync();

    if (join) {
        join();
    }

    return ok ? JNI_TRUE : JNI_FALSE;
}
