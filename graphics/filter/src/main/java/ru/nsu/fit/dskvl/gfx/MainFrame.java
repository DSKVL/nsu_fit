package ru.nsu.fit.dskvl.gfx;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.security.InvalidParameterException;

public class MainFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private final JMenuBar menuBar;
    protected JToolBar toolBar;

    public MainFrame() {
        try {
            FlatLightLaf.setup();
        } catch(Exception ignored) {}
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Main toolbar");
        toolBar.setRollover(true);
        add(toolBar, BorderLayout.PAGE_START);
    }

    public MainFrame(int x, int y, String title) {
        this();
        setSize(x, y);
        setLocationByPlatform(true);
        setTitle(title);
        setMinimumSize(new Dimension(x, y));
    }

    public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon,
                                    Runnable actionMethod) throws SecurityException {
        JMenuItem item = new JMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if(icon != null)
            item.setIcon(new ImageIcon(getClass().getResource(icon), title));
        item.addActionListener(e -> actionMethod.run());
        return item;
    }
    public JRadioButtonMenuItem createRadioButtonMenuItem(String title, String tooltip,
          int mnemonic, String icon, Runnable actionMethod) throws SecurityException {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if(icon != null)
            item.setIcon(new ImageIcon(getClass().getResource(icon), title));
        item.addActionListener(e -> actionMethod.run());
        return item;
    }

    public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, Runnable actionMethod) throws SecurityException {
        return createMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }

    public JMenu createSubMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    public void addSubMenu(String title, int mnemonic) {
        MenuElement element = getParentMenuElement(title);
        if(element == null)
            throw new InvalidParameterException("Menu path not found: "+title);
        JMenu subMenu = createSubMenu(getMenuPathName(title), mnemonic);
        if(element instanceof JMenuBar)
            ((JMenuBar)element).add(subMenu);
        else if(element instanceof JMenu)
            ((JMenu)element).add(subMenu);
        else if(element instanceof JPopupMenu)
            ((JPopupMenu)element).add(subMenu);
        else
            throw new InvalidParameterException("Invalid menu path: "+title);
    }

    public void addMenuItem(String title, String tooltip, int mnemonic, String icon,
                            Runnable actionMethod) throws SecurityException {
        MenuElement element = getParentMenuElement(title);
        if(element == null)
            throw new InvalidParameterException("Menu path not found: "+title);
        JMenuItem item = createMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
        if(element instanceof JMenu)
            ((JMenu)element).add(item);
        else if(element instanceof JPopupMenu)
            ((JPopupMenu)element).add(item);
        else
            throw new InvalidParameterException("Invalid menu path: "+title);
    }
    public JRadioButtonMenuItem addJRadioButtonMenuItem(String title, String tooltip, int mnemonic,
                                                        String icon, Runnable actionMethod) throws SecurityException {
        MenuElement element = getParentMenuElement(title);
        if(element == null)
            throw new InvalidParameterException("Menu path not found: "+title);
        JRadioButtonMenuItem item = createRadioButtonMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
        if(element instanceof JMenu)
            ((JMenu)element).add(item);
        else if(element instanceof JPopupMenu)
            ((JPopupMenu)element).add(item);
        else
            throw new InvalidParameterException("Invalid menu path: "+title);
        return item;
    }

    public void addMenuItem(String title, String tooltip, int mnemonic, Runnable actionMethod) throws SecurityException {
        addMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }

    public JRadioButtonMenuItem addJRadioButtonMenuItem(String title, String tooltip, int mnemonic, Runnable actionMethod) throws SecurityException {
        return addJRadioButtonMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }

    public void addMenuSeparator(String title) {
        MenuElement element = getMenuElement(title);
        if(element == null)
            throw new InvalidParameterException("Menu path not found: "+title);
        if(element instanceof JMenu)
            ((JMenu)element).addSeparator();
        else if(element instanceof JPopupMenu)
            ((JPopupMenu)element).addSeparator();
        else
            throw new InvalidParameterException("Invalid menu path: "+title);
    }

    private String getMenuPathName(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if(pos > 0)
            return menuPath.substring(pos+1);
        else
            return menuPath;
    }

    private MenuElement getParentMenuElement(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if(pos > 0)
            return getMenuElement(menuPath.substring(0, pos));
        else
            return menuBar;
    }

    public MenuElement getMenuElement(String menuPath) {
        MenuElement element = menuBar;
        for(String pathElement: menuPath.split("/")) {
            MenuElement newElement = null;
            for(MenuElement subElement: element.getSubElements()) {
                if((subElement instanceof JMenu && ((JMenu)subElement).getText().equals(pathElement))
                        || (subElement instanceof JMenuItem && ((JMenuItem)subElement).getText().equals(pathElement))) {
                    if(subElement.getSubElements().length==1 && subElement.getSubElements()[0] instanceof JPopupMenu)
                        newElement = subElement.getSubElements()[0];
                    else
                        newElement = subElement;
                    break;
                }
            }
            if(newElement == null) return null;
            element = newElement;
        }
        return element;
    }

    public JButton createToolBarButton(JMenuItem item) {
        JButton button = new JButton(item.getIcon());
        for(ActionListener listener: item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    public JButton createToolBarButton(JMenuItem item, ButtonGroup gr) {
        JButton button = new JButton(item.getIcon());
        gr.add(button);
        for(ActionListener listener: item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    public JToggleButton createToolBarToggleButton(JMenuItem item) {
        JToggleButton button = new JToggleButton(item.getIcon());
        button.setText(item.getText());
        for(ActionListener listener: item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    public JButton createToolBarButton(String menuPath) {
        JMenuItem item = (JMenuItem)getMenuElement(menuPath);
        if(item == null)
            throw new InvalidParameterException("Menu path not found: "+menuPath);
        return createToolBarButton(item);
    }

    public JToggleButton createToolBarToggleButton(String menuPath) {
        JMenuItem item = (JMenuItem)getMenuElement(menuPath);
        if(item == null)
            throw new InvalidParameterException("Menu path not found: "+menuPath);
        return createToolBarToggleButton(item);
    }

    public void addToolBarButton(String menuPath) {
        toolBar.add(createToolBarButton(menuPath));
    }

    //	public JButton addToolBarButton(String menuPath, ButtonGroup gr)
//	{
//		var button = createToolBarButton(menuPath, gr);
//		toolBar.add(button);
//		return button;
//	}
    public JToggleButton addToolBarToggleButton(String menuPath) {
        var button = createToolBarToggleButton(menuPath);
        toolBar.add(button);
        return button;
    }

    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }
}
