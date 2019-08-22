//
//  ViewController.swift
//  PPS-Data
//
//  Created by Joshua Ren on 2019-06-24.
//

import UIKit
import UserNotifications
import ResearchKit
import SafariServices

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        // Initialize notification capabilities
        registerForPushNotifications()
        createPushNotifications()
        listScheduledNotifications()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        
        //Hides the default parent level navigation bar upon navigating to a child screen
        navigationController?.setNavigationBarHidden(true, animated: true)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /*
     * Action: openEmail
     * Description: Opens the user's default email client, and an email within
     * the client addressed to the email address below.
     */
    @IBAction func openEmail(_ sender: Any) {
        let email = "renj@stanford.edu" // Email to be contacted
        if let url = URL(string: "mailto:\(email)") {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url)
            } else {
                UIApplication.shared.openURL(url)
            }
        }
    }
    
    /*
     * Function: openSurvey
     * Description: uses a SFSafariViewController to open a Qualtrics-based ESM.
     */
    @IBAction func openSurvey(_ sender: Any) {
        let urlString = NSURL(string: "https://stanforduniversity.qualtrics.com/jfe/form/SV_0P1d0g3k8oB6UV7")
        let svc = SFSafariViewController(url: urlString! as URL)
        self.present(svc, animated: true, completion: nil)
    }
    
    /*
     * Function: openDiary
     * Description: uses a SFSafariViewController to open a Qualtrics-based ESM.
     */
    @IBAction func openDiary(_ sender: Any) {
        let urlString = NSURL(string: "https://stanforduniversity.qualtrics.com/jfe/form/SV_etIfLk7J9jTgIIJ")
        let svc = SFSafariViewController(url: urlString! as URL)
        self.present(svc, animated: true, completion: nil)
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
    
    // Helper function for creating notifications
    func helpCreateNotification(contentTitle: String, contentBody: String, dateHour: Int, dateMinutes: Int) -> UNNotificationRequest {
        // notification content details
        let content = UNMutableNotificationContent()
        content.title = contentTitle
        content.body = contentBody
        content.sound    = .default
        
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
        //  create more requests by copy pasting to requests array if needed
        let requests = [
            helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 0, dateMinutes: 0),
            helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 6, dateMinutes: 0),
            helpCreateNotification(contentTitle: "ESM Survey", contentBody: "Time to take a survey! :)", dateHour: 12, dateMinutes: 0),
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
        
    }
    
    // prints all current pending notifications
    func listScheduledNotifications() {
        UNUserNotificationCenter.current().getPendingNotificationRequests { notifications in
            for notification in notifications {
                print(notification)
            }
        }
    }
}
