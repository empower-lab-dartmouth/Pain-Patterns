//
//  AppDelegate.swift
//  PPS-Data
//
//  Created by Joshua Ren on 2019-06-24.
//

import UIKit
import AWAREFramework
import Foundation
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    var core: AWARECore!
    var study: AWAREStudy!
    var manager: AWARESensorManager!

    //  Returns an instance of the current AppDelegate - this is used to access class-level
    //  variables of this AppDelegate in other files.
    static func shared() -> AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }
    
    //  Returns the URL of the AWARE study on which this application is running
    func getUrl() -> String {
        return "https://api.awareframework.com/index.php/webservice/index/2439/QPnWjaZXyx6l"
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        self.core = AWARECore.shared()                          //Initialize AWARE Core
        self.study = AWAREStudy.shared()                        //Initialize AWARE Study
        self.study.setDebug(false)                              //Debugging settings - turn off when running in production
        self.manager = AWARESensorManager.shared()              //Initialize AWARE Sensor Manager
        
        core.activate()
        core.requestPermissionForBackgroundSensing()            //Request permission to perform background sensing

        
        //  Declare, initialize AWARE sensors
        //  healthkit and iOSActivity are not available in
        //  the AWARE dashboard so they are created here
        let healthkit = AWAREHealthKit(awareStudy: self.study)
        let iOSActivity = IOSActivityRecognition(awareStudy: self.study)
        
        //  Setup background fetching interval for sensors
        //  Default is set up at the minimum background fetch
        UIApplication.shared.setMinimumBackgroundFetchInterval(UIApplication.backgroundFetchIntervalMinimum)
        
        //Add AWARE sensors to the sensor manager
        manager?.add(healthkit)
        manager?.add(iOSActivity)

        //  Set study url to the url listed on AWARE Dashboard
        let studyurl = getUrl()
        self.study?.setStudyURL(studyurl)
        
        self.study?.join(withURL: studyurl, completion: { (settings, studyState, error) in
            self.manager?.addSensors(with: self.study)              //Add sensors to study from AWARE study dashboard
            self.manager?.createDBTablesOnAwareServer()             //Initialize database for sensors
            self.manager?.startAllSensors()                         //Start sensors running
        })
        
        //  Initialize notification capabilities
        registerForPushNotifications()
        createPushNotifications()
        
        print("Setup complete.")

        return true
    }
    
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks.
    func applicationWillResignActive(_ application: UIApplication) {
        
        listScheduledNotifications()
        //Here we use this to sync up our data with AWARE.
        self.manager?.syncAllSensors()
    }
    
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    func applicationDidEnterBackground(_ application: UIApplication) {
        
        //Start sensors operating in the background
        self.manager?.startAllSensors()
    }
    
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    func applicationWillEnterForeground(_ application: UIApplication) {}
    
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    func applicationDidBecomeActive(_ application: UIApplication) {}
    
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    func applicationWillTerminate(_ application: UIApplication) {}
    
    
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
        content.sound = .default
        
        // notification sending times: per day
        var date = DateComponents()
        date.hour = dateHour
        date.minute = dateMinutes
        
        let uuidString = UUID().uuidString
        // repeats: true will repeat sending the at the specified time
        let trigger = UNCalendarNotificationTrigger(dateMatching: date, repeats: true)
        return UNNotificationRequest(identifier: uuidString, content: content, trigger: trigger) // return a basket containing notification details
    }
    
    // Called in order to actually send notifications
    // Details of the content and when to send are specified here
    // current implementation sends X notifications per day
    func createPushNotifications() {
        //  change the values for contentTitle, contentBody, dateHour, dateMinutes to alter the content of the notification and when it gets sent
        //  create more requests by copy pasting to requests array if needed
        let requests = [
            helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 18, dateMinutes: 0),
            ]
        // Schedule the request with the APN service by adding them to the UNUserNotificationCenter.current()
        for request in requests {
            UNUserNotificationCenter.current().add(request)  { (error) in
                if let error = error {
                    print("error in PPS reminder: \(error.localizedDescription)")
                }
            }
        }
        //see current set of notifications
        listScheduledNotifications()
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

    
    // prints all current pending notifications
    func listScheduledNotifications() {
        UNUserNotificationCenter.current().getPendingNotificationRequests { notifications in
            var count = 0
            for notification in notifications {
                print(notification)
                count += 1
            }
            print("\nnotification count: ", count, "\n")
        }
    }
}
