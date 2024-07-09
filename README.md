# AliExpressLoginDemo

 A demo of an app that can log in to AliExpress

## Goal

1. Learn some Java

2. Write a POC for an app which can automate AliExpress

## Problem

While working on a GUI app (Using Java Swing) I was trying to write, I discovered that when you open a browser using Selenium, when you go to log in, you get a CAPTCHA slider.

Even if you manually drag the slider (As a human), it will not let you log in.

## Solution

1. Execute the Chrome binary directly (eg using `chrome.exe --remote-debugging-port=9222 --user-data-dir=remote-profile`)

2. Prompt the user to log into this browser

3. Connect to this Chrome instance using Selenium once the user has logged in

4. Dump the cookies to a JSON file (See the `CookieJar` class - I can't believe I could not find something like this through searches. Lots of over-complicated examples trying to serialize the data themselves!)

Then, the app is "Set up".

We can then open a normal Selenium webdriver instance without calling the Chrome binary directly and load in the cookies, thus logging you in. Hell, this new instance *does not even need to be Chrome* - Firefox works ;)

## What else I tried

I tried pointing the non-DevTools browser instance to the same user-data-dir but I was getting an error saying `Could not remove old devtools port file. Perhaps the given user-data-dir at is still attached to a running Chrome or Chromium process`. I could not resolve it, so dumping and loading Cookies was the only way I could get it to work

## Credits

I am a total Java noob. I also haven't done much with Cookies or logging in (Or even browser automation really), so I did a *lot* of Googling

* [This SO post](https://stackoverflow.com/questions/8344776/can-selenium-interact-with-an-existing-browser-session)  was the start of the light at the end of the tunnel for me

* [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) made things a lot cleaner. I was really struggling with getting Chrome working in Selenium, and this makes it super simple. As an added bonus, it also removes the need for the end-user (I plan on distributing my app to friends if I get it working) to have to download ChromeDriver and stuff. Furthermore, it provides a getter to get the location of the Chrome executable, so I could call it to launch the DevTools instance, and not need any settings file or anything to store that.



## Notes

WebDriverManager is one of the main reasons for this repo. Initially I was having a problem with it being really slow to launch the browser instance, so I made a GUI app to demonstrate the issue, planning on posting it on their support forum, but then, magically, it started working just fine. Still no idea why.

Oh well, may as well package it up, hopefully it will serve as a useful example to someone else - I certainly could not find complete examples of doing this kind of thing, so hopefully it helps someone, somewhere.

I did mention I was a total Java noob, right? Fair warning - there's no error handling, probably janky code all over the shop - you have been warned.

If you know any better, I would welcome a PR.



## Usage

1. Launch the app

2. Click `Login with DevTools Chrome`
   The first time you do this, it will be like you just installed a fresh copy of Chrome (Setup page and all) - it's your normal Chrome instance, but seeing as I specified `--user-data-dir` as well as enabled the remote debugger, it's using a new profile.
   This is stored in `BrowserProfile` in the app's folder.
   This seemed like a sensible move, as then this app shouldn't have access to all your other cookies I guess?

3. Follow the instructions in the app's dialog (ie log in)

4. Click OK in the app's dialog

5. The cookies will be saved to `Cookies.json` and the DevTools Chrome instance will automatically close

6. You can now click `Open Chrome` or `Open Firefox` to open a "normal" Selenium browser

7. After that, the `Navigate to checkout`demonstrates automation of the currently open browser while logged in
