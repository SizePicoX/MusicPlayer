package GUI;

import javax.swing.*;

class MyMenuItem extends JMenuItem {

    /**
     * 标记鼠标是否进入了对应的按钮区域.为true则代表鼠标进入了相应的按钮区域.
     * 当全部为false时,菜单将被置为不可见.
     */
    boolean flag = true;

    MyMenuItem(String text){
        super(text);
    }
}
