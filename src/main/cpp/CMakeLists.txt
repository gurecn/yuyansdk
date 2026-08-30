cmake_minimum_required(VERSION 3.22.1)
project("yuyansyncbridge")

add_library(yuyansyncbridge SHARED rime_sync_bridge.cpp)

find_library(log-lib log)
find_library(dl-lib dl)

target_link_libraries(yuyansyncbridge ${log-lib} ${dl-lib})
