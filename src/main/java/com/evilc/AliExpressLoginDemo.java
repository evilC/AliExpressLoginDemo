package com.evilc;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

/*
Demo of being able to automate AliExpress using Selenium
The problem is that AliExpress seems to detect that the browser is being automated...
... and pops up a CAPTCHA (Slider).
Even if you manually log in and manually drag the slider, it refuses to log in.
This code demonstrates opening a Chrome browser in Dev Tools mode...
... Pausing the app, allowing the user to manually log in ...
... and then connecting to the browser, allowing you to automate it
*/
public class AliExpressLoginDemo extends JPanel implements ActionListener
{
    private static WebDriver driver;
    private CookieJar cookieJar;
    private static JFrame mainFrame;

    public static void main( String[] args )
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("AliExpress Login Demo");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.add(new AliExpressLoginDemo());

        mainFrame.pack();
        mainFrame.setSize(320, 200);
        mainFrame.setVisible(true);
    }

    public AliExpressLoginDemo(){
        super(new GridLayout(0,1));
        cookieJar = new CookieJar("Cookies.json");

        buildButton("Login with DevTools Chrome", "openDevToolsChrome");
        buildButton("Open Chrome", "openChrome");
        buildButton("Open Firefox", "openFirefox");
        buildButton("Navigate to checkout", "navigateToCheckout");
    }

    public void buildButton(String text, String action){
        JButton button = new JButton(text);
        button.setActionCommand(action);
        button.addActionListener(this);
        add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String eventName = e.getActionCommand();
        switch (eventName) {
            case "openDevToolsChrome":
                openDevToolsChrome();
                break;
        
            case "openChrome":
                openChrome();
                break;

            case "openFirefox":
                openFirefox();
                break;

            case "navigateToCheckout":
                navigateToCheckout();
                break;

            default:
                break;
        }
    }

    // Launch chrome in Dev Tools mode - NOT via the WebDriver!
    // AliExpress CAPTCHA does not appear in this mode
    private void openDevToolsChrome() {
        // Create a folder to house the chrome user profile, if it does not already exist
        File browserProfileDir = new File("./BrowserProfile");
        if (!browserProfileDir.exists()){
            browserProfileDir.mkdirs();
        }
        // Get the absolute path of the user profile
        String browserProfilePath = browserProfileDir.getAbsolutePath();

        // Get path of chrome executable
        String browserPath = WebDriverManager.chromedriver().getBrowserPath().get().toString();

        // Launch chrome in Remote Debugging mode
        ProcessBuilder pb=new ProcessBuilder("\"" + browserPath + "\" Aliexpress.com --remote-debugging-port=9222 --user-data-dir=\"" + browserProfilePath + "\"");
        pb.redirectErrorStream(true);
        try {
            pb.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Display dialog prompting user to log in
        JOptionPane.showMessageDialog(mainFrame, "Log in to the browser\n"
        + "Make sure you have an item in the cart\n"
        + "Then click OK\n"
        + "DO NOT CLOSE THE BROWSER"
        + "");        

        // Set up the ChromeDriver to connect to the existing Dev Tools chrome instance
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        // Save the login the cookies
        cookieJar.saveCookies(driver);

        // Close the browser
        driver.close();
    }

    private void openChrome() {
        // Spawn a new copy of Chrome not using the dev tools
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        positionBrowser();
        loadCookies();
    }

    public void openFirefox(){
        // Spawn a new copy of Firefox not using the dev tools
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        positionBrowser();
        loadCookies();
    }

    public void positionBrowser(){
        driver.manage().window().setPosition(new Point(320, 0));
        driver.manage().window().setSize(new Dimension(1600, 1024));
    }
    
    // Load the cookies that were saved from the Chrome Dev Tools window
    public void loadCookies(){
        // Go to domain, so cookies can be applied
        driver.get("https://www.aliexpress.com");
        // Load cookies
        cookieJar.loadCookies(driver);
        // Re-get the page to get it to update and show you as logged in
        driver.get("https://www.aliexpress.com");
    }

    public void navigateToCheckout()
    {
        // Navigate to the cart page
        driver.get("https://www.aliexpress.com/p/shoppingcart/index.html");
        // Click the checkout button
        driver.findElement(By.xpath("//button[contains(@class,'cart-summary-button')]")).click();
    }
}
