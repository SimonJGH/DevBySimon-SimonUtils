package com.simon.utils.widget.recycler.interfaces;


import com.simon.utils.widget.recycler.SwipeMenu;

/**
 * SwipeMenuCreator
 */
public interface SwipeMenuCreator {

    /**
     * Create menu for recyclerVie item.
     *
     * @param swipeLeftMenu  The menu on the left.
     * @param swipeRightMenu The menu on the right.
     * @param viewType       The view type of the new view.
     */
    void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType);

}
