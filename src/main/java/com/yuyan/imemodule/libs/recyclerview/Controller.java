package com.yuyan.imemodule.libs.recyclerview;

interface Controller {

    /**
     * The menu is open?
     *
     * @return true, otherwise false.
     */
    boolean isMenuOpen();

    /**
     * The menu is open on the left?
     *
     * @return true, otherwise false.
     */
    boolean isLeftMenuOpen();

    /**
     * The menu is open on the right?
     *
     * @return true, otherwise false.
     */
    boolean isRightMenuOpen();

    /**
     * The menu is completely open on the left?
     *
     * @return true, otherwise false.
     */
    boolean isLeftCompleteOpen();

    /**
     * The menu is completely open on the right?
     *
     * @return true, otherwise false.
     */
    boolean isRightCompleteOpen();

    /**
     * The menu is open?
     *
     * @return true, otherwise false.
     */
    boolean isMenuOpenNotEqual();

    /**
     * The menu is open on the left?
     *
     * @return true, otherwise false.
     */
    boolean isLeftMenuOpenNotEqual();

    /**
     * The menu is open on the right?
     *
     * @return true, otherwise false.
     */
    boolean isRightMenuOpenNotEqual();

    /**
     * Open the current menu.
     */
    void smoothOpenMenu();

    /**
     * Smooth closed the menu.
     */
    void smoothCloseMenu();

    /**
     * Smooth closed the menu for the duration.
     *
     * @param duration duration time.
     */
    void smoothCloseMenu(int duration);

}