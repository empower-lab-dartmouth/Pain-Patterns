//
//  AppDelegate.swift
//  PPS-Data
//
//  Created by Joshua Ren on 2019-06-24.
//

import UIKit
import Foundation
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    //  Returns an instance of the current AppDelegate - this is used to access class-level
    //  variables of this AppDelegate in other files.
    static func shared() -> AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        //  Setup background fetching interval for sensors
        //  Default is set up at the minimum background fetch
        UIApplication.shared.setMinimumBackgroundFetchInterval(UIApplication.backgroundFetchIntervalMinimum)
        
        //  Initialize notification capabilities
        registerForPushNotifications()
        createPushNotifications()
        
        print("Setup complete.")
        
        return true
    }
    
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks.
        
        //// Here we use this to sync up our data.
    }
    
   
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        
//         Start sensors operating in the background
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
         // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
//        self.manager?.startAllSensors()
    }
    
   
    func applicationDidBecomeActive(_ application: UIApplication) {
         // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }
    
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground.
//        self.manager?.startAllSensors()
//        self.manager?.syncAllSensors()
    }
    
    // Called in order to get permissions to send notificatoins
    func registerForPushNotifications() {
        UNUserNotificationCenter.current()
            .requestAuthorization(options: [.alert, .sound, .badge]) {
                [weak self] granted, error in
                
                print("Permission granted: \(granted)")
                guard granted else { return }
                self?.getNotificationSettings()
        }
    }
    
    // Helper function for creating notifications
    func helpCreateNotification(contentTitle: String, contentBody: String, dateHour: Int, dateMinutes: Int) -> UNNotificationRequest {
        // notification content details
        let content = UNMutableNotificationContent()
        content.title = contentTitle
        content.body = contentBody
        
        // notification sending times: per day
        var date = DateComponents()
        date.hour = dateHour
        date.minute = dateMinutes
        
        let uuidString = UUID().uuidString                                                  // string representation of the NSUUID object
        let trigger = UNCalendarNotificationTrigger(dateMatching: date, repeats: true)      // repeats: true will repeat sending the notification                                                                                     at the specified time
        return UNNotificationRequest(identifier: uuidString, content: content, trigger: trigger) // return a basket containing notification details
    }
    
    // Called in order to actually send notifications
    // Details of the content and when to send are specified here
    // current implementation sends X notifications per day
    func createPushNotifications() {
        //  change the values for contentTitle, contentBody, dateHour, dateMinutes to alter the content of the notification and when it gets sent
        //  create more requests and add to notification center if needed
        let request = helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 0, dateMinutes: 0)
        let request2 = helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 6, dateMinutes: 0)
        let request3 = helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 12, dateMinutes: 0)
        let request4 = helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 18, dateMinutes: 0)
        
        // Schedule the request with the APN service by adding them to the notificationCenter
        let notificationCenter = UNUserNotificationCenter.current()
        notificationCenter.add(request) { (error) in
            if error != nil { print(error) }
        }
        notificationCenter.add(request2) { (error) in
            if error != nil { print(error) }
        }
        notificationCenter.add(request3) { (error) in
            if error != nil { print(error) }
        }
        notificationCenter.add(request4) { (error) in
            if error != nil { print(error) }
        }
    }
    
    // Called to make sure notifications are allowed
    func getNotificationSettings() {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            print("Notification settings: \(settings)")
            guard settings.authorizationStatus == .authorized else { return }
            DispatchQueue.main.async {
                UIApplication.shared.registerForRemoteNotifications()
            }
        }
    }
    
    // Called to print device id when device registers for notifications
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
        ) {
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        let token = tokenParts.joined()
        print("Device Token: \(token)")
    }
    
    // Called when error is encountered when registering for notifications
    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register: \(error)")
    }
    
}
