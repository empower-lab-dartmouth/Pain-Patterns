//
//  Notifcations.swift
//  PPS-Data
//
//  Created by Joshua Ren on 10/28/19.
//  Copyright © 2019 Joshua Ren. All rights reserved.
//

import Foundation
import UserNotifications

// Called in order to get permissions to send notificatoins
func registerForPushNotifications() {
    UNUserNotificationCenter.current()
        .requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            guard granted else {
                print("notification permission not granted")
                return
            }
            getNotificationSettings()
            createPushNotifications()
    }
    
}

// Helper function for creating notifications
// Called in order to actually send notifications
func helpCreateNotification(contentTitle: String, contentSubTitle: String, contentBody: String, dateHour: Int, dateMinutes: Int) {
    
    // notification sending times: per day
    var date = DateComponents()
    date.hour = dateHour
    date.minute = dateMinutes
            
    // notification content details
    let content = UNMutableNotificationContent()
    content.title = "Survey"
    content.subtitle = ""
    content.body = contentBody
    content.sound = .default
        
    // unique ID for notification
    let uuidString = UUID().uuidString
    
    let trigger = UNCalendarNotificationTrigger(dateMatching: date, repeats: true)      // repeats: true will repeat sending the notification                                                                                     at the specified time
    let request = UNNotificationRequest(identifier: uuidString, content: content, trigger: trigger) // return a basket containing notification details
    UNUserNotificationCenter.current().add(request)  { (error) in
        if let error = error {
            print("error in PPS reminder: \(error.localizedDescription)")
        }
    }
}

// Details of the content and when to send are specified here
// Depending on how many notifications you have change the number and content in the arrays: notificationTitles, notificationBodies, dateHours, dateMinutes
// make sure all arrays are the same length. use "" if needed
// current implementation sends X number of notifications per day
// if you want to enable auto openSurvey capabilities make sure to include "survey" or "Survey" in title for surveys
// and "diary" or "Diary" for diaries
func createPushNotifications() -> [String : [[Int: Int]]] {
    
    //  change the values for contentTitle, contentBody, dateHour, dateMinutes to alter the content of the notification and when it gets sent
    let contentTitles = ["Survey", "Survey", "Survey", "Survey", "Diary"]
    let contentSubTitles = ["", "", "", "", ""]
    let contentBodies = ["Time to take a survey! :)", "Time to take a survey! :)", "Time to take a survey! :)", "Time to take a survey! :)", "Diary Time :)"]
    let dateHours = [0, 6, 12, 18, 11]
    let dateMinutes = [0, 0, 0, 0, 0]
    
    // make sure all arrays are the same length
    if ((contentTitles.count != contentSubTitles.count) || (contentSubTitles.count != contentBodies.count) || (contentBodies.count != dateHours.count) || (dateHours.count != dateMinutes.count)) {
        print("\nERROR: lengths of arrays do not match\n")
        abort()
    }
    
    // notifications are created here
    UNUserNotificationCenter.current().getPendingNotificationRequests() { notifications in
        if (notifications.count == 0) {
            for i in 0..<contentTitles.count {
                helpCreateNotification(contentTitle: contentTitles[i], contentSubTitle: contentSubTitles[i], contentBody: contentBodies[i], dateHour: dateHours[i], dateMinutes: dateMinutes[i])
            }
        } else {
//            use this removeNotifications if you want to remove notifications
//            removeNotifcations()
        }
    }
    
    return helpGetHours(titles: contentTitles, dateHours: dateHours, dateMinutes: dateMinutes)
}

// Called to make sure notifications are allowed
func getNotificationSettings() {
    UNUserNotificationCenter.current().getNotificationSettings { settings in
        guard settings.authorizationStatus == .authorized else { return }
    }
}

// creates dicts that map notification titles to sending times
// uses trigger times useful for auto opening surveys
func helpGetHours(titles: [String], dateHours: [Int], dateMinutes: [Int]) -> [String : [[Int: Int]]] {
    var nameToTimes = [String : [[Int : Int]]]()
    for i in 0..<dateHours.count {
        let curTitle = titles[i]
        let curHour = dateHours[i]
        let curMin = dateMinutes[i]
        var timeComponents = [Int: Int]()
        timeComponents[curHour] = curMin
        if (nameToTimes[curTitle] == nil) { // if this is first entry of this type
            nameToTimes[curTitle] = [timeComponents]
        } else {
            nameToTimes[curTitle]?.append(timeComponents)
        }
    }
    return nameToTimes
}

// gets notification times by utilizing return value of createPushNotifications()
func getNotificationTimes() -> [String : [[Int: Int]]] {
    return createPushNotifications()
}

// use this function to remove all notifications from this app
func removeNotifications() {
    UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
    UNUserNotificationCenter.current().removeAllDeliveredNotifications()
}

// use this function to view current pending notifications
func listPendingNotifications() {
    UNUserNotificationCenter.current().getPendingNotificationRequests() { notifications in
        for notification in notifications {
            print(notification)
        }
        print("existing notifications count: ", notifications.count, "\n")
    }
}

