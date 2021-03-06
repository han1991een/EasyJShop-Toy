package cc.openhome.menu;


import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;

import cc.openhome.img.ScreenCaptureHelper;
import cc.openhome.main.AbstractChild;
import cc.openhome.main.CanvasComponent;
import cc.openhome.main.ColorDemoBox;
import cc.openhome.main.IBatcher;

public class ImageMenu extends AbstractChild {
    private ScreenCaptureHelper captureHelper;
    
    private JMenu imageMenu;
    
    private JMenuItem captureMenuItem, newImageMenuItem;
    private JMenuItem openMenuItem, saveMenuItem, saveAsMenuItem, saveAllMenuItem;
    private JMenuItem exitMenuItem;
    
    private JSlider delaySlider;
    
    private JSpinner widthSpinner, heightSpinner;
    private ColorDemoBox backgroundColorBox;
    private JPanel newImagePanel;
    
    private JFileChooser openFileChooser, saveFileChooser;
    
    private WindowListener windowListener;
    
    private InternalFrameListener internalFrameListener;
    
    public ImageMenu() {
        initResource();
        setupUIComponent();
        setupEventListener();
    }
    
    private void initResource() {
        
        
        try {
            captureHelper = new ScreenCaptureHelper();
        }
        catch(AWTException e) {
            infoMessageBox(e.getMessage());
        }
    }
    
    private void setupUIComponent() {
        delaySlider = new JSlider(SwingConstants.HORIZONTAL, 0, 20, 0);
        delaySlider.setPaintTrack(true);
        delaySlider.setPaintLabels(true);
        delaySlider.setMajorTickSpacing(5);
        delaySlider.setMajorTickSpacing(1);
        delaySlider.setPaintTicks(true);
        
        openFileChooser = new JFileChooser();
        openFileChooser.setMultiSelectionEnabled(true);
        openFileChooser.addChoosableFileFilter(new OpenableFileFilter());
        
        saveFileChooser = new JFileChooser();
        saveFileChooser.addChoosableFileFilter(new SavableFileFilter());
        
        imageMenu = new JMenu("Image");
        captureMenuItem = new JMenuItem("Get screen");
        newImageMenuItem = new JMenuItem("New");
        openMenuItem = new JMenuItem("Open..");
        saveMenuItem = new JMenuItem("Save");
        saveAsMenuItem = new JMenuItem("Save as..");
        saveAllMenuItem = new JMenuItem("Save all");
        exitMenuItem = new JMenuItem("Exit");
        
        imageMenu.add(captureMenuItem);
        imageMenu.add(newImageMenuItem);
        imageMenu.addSeparator();
        imageMenu.add(openMenuItem);
        imageMenu.add(saveMenuItem);
        imageMenu.add(saveAsMenuItem);
        imageMenu.add(saveAllMenuItem);
        imageMenu.addSeparator();
        imageMenu.add(exitMenuItem);
        
        saveMenuItem.setEnabled(false);
        saveAsMenuItem.setEnabled(false);
        saveAllMenuItem.setEnabled(false);
        
        widthSpinner = new JSpinner();
        widthSpinner.setValue(new Integer(640));
        heightSpinner = new JSpinner();
        heightSpinner.setValue(new Integer(480));
        backgroundColorBox = new ColorDemoBox(Color.white);
        newImagePanel = new JPanel();
        newImagePanel.add(new JLabel("Width"));
        newImagePanel.add(widthSpinner);
        newImagePanel.add(new JLabel("Height"));
        newImagePanel.add(heightSpinner);
        newImagePanel.add(new JLabel(" Background color"));
        newImagePanel.add(backgroundColorBox);
    }
    
    private void setupEventListener() {
        captureMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
        newImageMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        openMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        saveMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        exitMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        
        captureMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        captureScreen();
                    }
                }
            );

        newImageMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        newImageFile();
                    }
                }
            );
        
        openMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {                        
                        openImageFile();
                    }
                }
            );
        
        saveMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveImageFile();
                    }
                }
            );
        
        saveAsMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveImageFileAs();
                    }
                }
            );
        
        saveAllMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveAllImageFile();
                    }
                }
            );
        
        exitMenuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        checkUnsavedImages();
                    }
                }
            );
        
        widthSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(((Integer) widthSpinner.getValue()).intValue() <= 0) {
                    widthSpinner.setValue(new Integer(1));
                }
            }
        });
        
        heightSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(((Integer) heightSpinner.getValue()).intValue() <= 0) {
                    heightSpinner.setValue(new Integer(1));
                }
            }
        });
        
        backgroundColorBox.addMouseListener(new MouseAdapter() {           
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(null, "Color information", backgroundColorBox.getColor());
                if(color != null) {
                    backgroundColorBox.setColor(color);
                    backgroundColorBox.repaint();
                }
            }
        });
        
        windowListener = new WindowListener() {
            public void windowClosing(WindowEvent e) {
                checkUnsavedImages();
            }

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        };
        
        internalFrameListener = new InternalFrameListener() {

            public void internalFrameOpened(InternalFrameEvent e) {
                saveMenuItem.setEnabled(true);
                saveAsMenuItem.setEnabled(true);
                saveAllMenuItem.setEnabled(true);
            }

            public void internalFrameClosing(InternalFrameEvent e) {
                JInternalFrame internalFrame = (JInternalFrame) e.getSource();//getDesktopPane().getSelectedFrame();
                
                try {
                    internalFrame.setIcon(false);
                    internalFrame.setSelected(true);
                }
                catch(PropertyVetoException ex) {
                    infoMessageBox(ex.getMessage());
                }
                
                checkUnsavedImage(internalFrame);
            }

            public void internalFrameClosed(InternalFrameEvent e) {
                checkImageMenuItem();
            }

            public void internalFrameIconified(InternalFrameEvent e) {
                checkImageMenuItem();
            }
            public void internalFrameDeiconified(InternalFrameEvent e) {
                checkImageMenuItem();
            }
            public void internalFrameActivated(InternalFrameEvent e) {
                checkImageMenuItem();
            }
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
    }
    
    private void checkImageMenuItem() {
        if(getDesktopPane().getAllFrames().length == 0 || getDesktopPane().getSelectedFrame() == null) {
            saveMenuItem.setEnabled(false);
            saveAsMenuItem.setEnabled(false);
            saveAllMenuItem.setEnabled(false);
        }
        else {
            saveMenuItem.setEnabled(true);
            saveAsMenuItem.setEnabled(true);
            saveAllMenuItem.setEnabled(true);
        }
    }
    
    public JMenu getMenu() {
        return imageMenu;
    }
    
    private void captureScreen() {
        parent.setVisible(false);
        int option = JOptionPane.showOptionDialog(null, 
                delaySlider, "delay ? (seconds)", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, smallLogo, null, null);
        
        if(option == JOptionPane.CANCEL_OPTION) {
            parent.setVisible(true);
            return;
        }
        
        try {
            Thread.sleep(delaySlider.getValue() * 1000);
        }
        catch(InterruptedException e) {
            infoMessageBox(e.getMessage());
        }
        
        Image image = captureHelper.capture();
        
        parent.setVisible(true);
        
        newInternalFrame("*untitled", image);
    }
    
    private void newImageFile() {
        int option = JOptionPane.showOptionDialog(null, newImagePanel, "New image", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, smallLogo, null, null);
        
        if(option == JOptionPane.OK_OPTION) {
            int width = ((Integer) widthSpinner.getValue()).intValue();
            int height = ((Integer) heightSpinner.getValue()).intValue();
            BufferedImage bufferedImage = new BufferedImage(width , height, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.getGraphics();
            g.setColor(backgroundColorBox.getColor());
            g.fillRect(0, 0, width, height);
            newInternalFrame("untitled", bufferedImage);
        }
    }
    
    private void newInternalFrame(String title, Image image) {
        JInternalFrame internalFrame = createImageInternalFrame(title, image);

        getDesktopPane().add(internalFrame);
        
        internalFrame.setVisible(true);
        
        try {
            internalFrame.setSelected(true);
        }
        catch(PropertyVetoException e) {
            infoMessageBox(e.getMessage());
        }
        
        fitAppSize(image);
    }
    
    private void openImageFile() {
        new Thread(new Runnable() {
            public void run() {
                if(openFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
                    File[] files = openFileChooser.getSelectedFiles();
                    
                    for(int i = 0; i < files.length; i++) {
                        // bring it to top
                        try {
                            Image image = ImageIO.read(files[i]);
                            JInternalFrame internalFrame = createImageInternalFrame(files[i].getAbsolutePath(), image);
                            
                            getDesktopPane().add(internalFrame);
                            
                            internalFrame.setVisible(true);
                            internalFrame.setSelected(true);

                            fitAppSize(image);
                        }
                        catch(IOException e) {
                            infoMessageBox(e.getMessage());
                        }
                        catch(PropertyVetoException e) {
                            infoMessageBox(e.getMessage());
                        }
                    }
                }
            }
        }).start();
    }
    
    private void saveImageFile() {
        String title = getDesktopPane().getSelectedFrame().getTitle();
        
        if(title.equals("*untitled") || title.equals("untitled")) {
            saveImageFileAs();
        }
        else if(title.startsWith("*")){
            save(title.substring(1));
        }
    }
    
    private void saveImageFileAs() {
        if(saveFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = saveFileChooser.getSelectedFile();
            String filename = file.toString();
            String lowerCaseFilename = filename.toLowerCase();
            // the default extension filename is 'jpg'.
            if(!lowerCaseFilename.endsWith(".jpg") && !lowerCaseFilename.endsWith(".png")) {
                filename = filename + ".jpg";
                file = new File(filename);
            }
            
            if(file.exists()) {
                boolean isSaveAs = true;
                
                while(isSaveAs) {
                    int option = JOptionPane.showOptionDialog(null, 
                            filename + " exists, overwrite?", "overwrite?", JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE, smallLogo, null, null);
                    
                    switch(option) {
                        case JOptionPane.YES_OPTION:
                            isSaveAs = false;
                            break;                   
                        case JOptionPane.NO_OPTION:
                            if(saveFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                                file = saveFileChooser.getSelectedFile();
                                filename = file.toString();
                            }
                            else {
                                isSaveAs = false;
                            }
                            break;
                        case JOptionPane.CANCEL_OPTION:
                            // cancell save action
                            return;     
                    }   
                }
            }
            
            save(filename);
        }        
    }
    
    private void save(String filename) {
        CanvasComponent canvas = getCanvasOfSelectedFrame();
        Image image = canvas.getImage();

        // create BufferedImage for ImageIO
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), 
                image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        
        try {
            // decide the extension filename
            int dotpos = filename.lastIndexOf('.');
            ImageIO.write(bufferedImage, filename.substring(dotpos + 1), new File(filename));
        }
        catch(IOException e) {
            infoMessageBox(e.getMessage());
        }
        
        getDesktopPane().getSelectedFrame().setTitle(filename);
    }
    
    private void saveAllImageFile() {
        if(getDesktopPane().getAllFrames().length == 0)
            return;
        
        final IBatcher batcher = new IBatcher() {
            public void execute() {
                saveImageFile();
            }
        };
        
        new Thread(new Runnable() {
            public void run() {
                batch(batcher);
            }
        }).start();
    }
    
    private void checkUnsavedImages() {
        JInternalFrame[] internalFrames = getDesktopPane().getAllFrames();
        
        for(int i = 0; i < internalFrames.length; i++) {
            try {
                internalFrames[i].setIcon(false);
                internalFrames[i].setSelected(true);
                if(checkUnsavedImage(internalFrames[i])) {
                    return;
                }
            }
            catch(PropertyVetoException e) {
                infoMessageBox(e.getMessage());
            }
        }
        
        // check all images ok, now quit the application.
        if(getDesktopPane().getAllFrames().length == 0)
            System.exit(0);
    }

    private boolean checkUnsavedImage(JInternalFrame internalFrame) {
        boolean cancel = false;
        
        if(internalFrame.getTitle().startsWith("*")) {
            int option = JOptionPane.showOptionDialog(null, 
                    internalFrame.getTitle().substring(1) + " is unsaved, save?", "save?", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, smallLogo, null, null);
            
            switch(option) {
                case JOptionPane.YES_OPTION:
                    try {
                        internalFrame.setSelected(true);
                        saveImageFile();
                        
                        if(!internalFrame.getTitle().startsWith("*")) {
                            removeMementoAndDispose(internalFrame);
                        }
                    }
                    catch(PropertyVetoException e) {
                        infoMessageBox(e.getMessage());
                    }
                    break;                   
                case JOptionPane.NO_OPTION:
                    removeMementoAndDispose(internalFrame);
                    break;
                case JOptionPane.CANCEL_OPTION:
                    // cancell quit action
                    cancel = true;
                    break;
            }   
        }
        else {
            removeMementoAndDispose(internalFrame);
        }
        
        return cancel;
    }
    
    private void removeMementoAndDispose(JInternalFrame internalFrame) {
        CanvasComponent canvas = getCanvasOfInternalFrame(internalFrame);
        getMementoManagers().remove(canvas);
        internalFrame.setVisible(false);
        internalFrame.dispose();
    }
    
    public InternalFrameListener getInternalFrameListener() {
        return internalFrameListener;
    }
    
    public WindowListener getWindowListener() {
        return windowListener;
    }
}
