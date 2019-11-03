//
//  HK-Tools.swift
//  PPS-Data
//
//  Created by Joshua Ren on 10/24/19.
//  Copyright © 2019 Joshua Ren. All rights reserved.
//

import Foundation
import HealthKit

func healthQuan() {
    let heartRate = HKObjectType.quantityType(forIdentifier: .heartRate)!
}

func healthKitData() {
    // if healthkit data is available upload it to firebase
    if HKHealthStore.isHealthDataAvailable() {
        let healthStore = HKHealthStore()
        let allTypes = Set([HKObjectType.workoutType(),
                            HKObjectType.quantityType(forIdentifier: .activeEnergyBurned)!,
                            HKObjectType.quantityType(forIdentifier: .distanceCycling)!,
                            HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!,
                            ])

        healthStore.requestAuthorization(toShare: allTypes, read: allTypes) { (success, error) in
            if !success {
                print("error in healthStore authorization access.")
            } else {
                for type in allTypes {
                    print(type)
                }
            }
        }
    }
}
