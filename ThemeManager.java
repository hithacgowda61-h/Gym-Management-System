package miniproject;

import java.awt.Color;

import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class ThemeManager {
    private static boolean dark = true; 
 public static void toggle() { dark = !dark; }//switch theme
    public static boolean isDark() { return dark; }
 public static Color bg() { return dark ? new Color(18,18,18) : Color.WHITE; }
    public static Color card() { return dark ? new Color(30,30,30) : new Color(245,245,245); }
    public static Color text() { return dark ? Color.WHITE : Color.BLACK; }//text color
    public static Color accent() { return dark ? new Color(200,30,30) : new Color(30,110,220); }//button color
    public static Color btnBg() { return dark ? new Color(50,50,50) : new Color(230,230,230); }
    //apply theme to full window
 public static void applyFrame(Window window) {
        if (window instanceof JFrame) {
            ((JFrame) window).getContentPane().setBackground(bg());
        } 
        else if (window instanceof JDialog) {
            ((JDialog) window).getContentPane().setBackground(bg());
        }
    }
public static void styleButton(JButton b) {
        b.setBackground(accent());
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }
}
