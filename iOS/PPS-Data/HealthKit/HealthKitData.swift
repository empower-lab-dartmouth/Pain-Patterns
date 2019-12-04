//
//  HK-Tools.swift
//  PPS-Data
//
//  Created by Joshua Ren on 10/24/19.
//  Copyright © 2019 Joshua Ren. All rights reserved.
//

import Foundation
import HealthKit
import Firebase


func getHealthKitData(ref: DatabaseReference, appId: String) {
    // if healthkit data is available upload it to firebase
    if HKHealthStore.isHealthDataAvailable() {
        let healthStore = HKHealthStore()
        let quanTypes = healthQuantityDataTypes()

        healthStore.requestAuthorization(toShare: nil, read: quanTypes) { (success, error) in
            guard success else { // if access is not granted
                let baseMessage = "error in healthStore authorization access"
                if let error = error {
                  print("\(baseMessage). Reason: \(error.localizedDescription)")
                } else {
                  print(baseMessage)
                }
                return
            }
            
            for type in quanTypes {
                let query = HKSampleQuery(sampleType: type, predicate: nil, limit: HKObjectQueryNoLimit, sortDescriptors: nil) { (query, samples, error) in
                    
                    if error != nil {
                        print("An error occured while setting up the \(type) observer. \(error!.localizedDescription)")
                        abort()
                    }
                    
                    let sample = samples?.last
                    if (sample != nil) {
                        let convertedSample = sample as! HKQuantitySample
                        
                        let sampleType = query.objectType?.identifier
                        let sampleDate = dealWithDate(date: sample!.endDate)
                        // update firebase latest entries
                        // YEET YEET YEET
                    }
                }
                healthStore.execute(query)
            }
            
        }
    }
}

func dealWithDate(date: Date) -> Double {
    var interval = Double()
    interval = date.timeIntervalSince1970
    return interval
}

// Quantity Types
func healthQuantityDataTypes() -> Set<HKQuantityType> {
    var typesToUse: Set<HKQuantityType> = [] // this is the set that contains all information you want to get
    
    // body measurements
    let height = HKObjectType.quantityType(forIdentifier: .height)!
    typesToUse.insert(height)
    let bodyMass = HKObjectType.quantityType(forIdentifier: .bodyMass)!
    typesToUse.insert(bodyMass)
    let bodyMassIndex = HKObjectType.quantityType(forIdentifier: .bodyMassIndex)!
    typesToUse.insert(bodyMassIndex)
    let leanBodyMass = HKObjectType.quantityType(forIdentifier: .leanBodyMass)!
    typesToUse.insert(leanBodyMass)
    let bodyFatPercentage = HKObjectType.quantityType(forIdentifier: .bodyFatPercentage)!
    typesToUse.insert(bodyFatPercentage)
    let waistCircumference = HKObjectType.quantityType(forIdentifier: .waistCircumference)!
    typesToUse.insert(waistCircumference)
    
    // Vital Signs
    let heartRate = HKObjectType.quantityType(forIdentifier: .heartRate)!
    typesToUse.insert(heartRate)
    let restingHeartRate = HKObjectType.quantityType(forIdentifier: .restingHeartRate)!
    typesToUse.insert(restingHeartRate)
    let walkingHeartRateAverage = HKObjectType.quantityType(forIdentifier: .walkingHeartRateAverage)!
    typesToUse.insert(walkingHeartRateAverage)
    let heartRateVariability = HKObjectType.quantityType(forIdentifier: .heartRateVariabilitySDNN)!
    typesToUse.insert(heartRateVariability)
    let oxygenSaturation = HKObjectType.quantityType(forIdentifier: .oxygenSaturation)!
    typesToUse.insert(oxygenSaturation)
    let bodyTemperature = HKObjectType.quantityType(forIdentifier: .bodyTemperature)!
    typesToUse.insert(bodyTemperature)
    let bloodPressureDiastolic = HKObjectType.quantityType(forIdentifier: .bloodPressureDiastolic)!
    typesToUse.insert(bloodPressureDiastolic)
    let bloodPressureSystolic = HKObjectType.quantityType(forIdentifier: .bloodPressureSystolic)!
    typesToUse.insert(bloodPressureSystolic)
    let respiratoryRate = HKObjectType.quantityType(forIdentifier: .respiratoryRate)!
    typesToUse.insert(respiratoryRate)
    let vo2Max = HKObjectType.quantityType(forIdentifier: .vo2Max)!
    typesToUse.insert(vo2Max)
    
    // Lab and Test Results
    let bloodAlcoholContent = HKObjectType.quantityType(forIdentifier: .bloodAlcoholContent)!
    typesToUse.insert(bloodAlcoholContent)
    let bloodGlucose = HKObjectType.quantityType(forIdentifier: .bloodGlucose)!
    typesToUse.insert(bloodGlucose)
    let electrodermalActivity = HKObjectType.quantityType(forIdentifier: .electrodermalActivity)!
    typesToUse.insert(electrodermalActivity)
    let forcedExpiratoryVolume1 = HKObjectType.quantityType(forIdentifier: .forcedExpiratoryVolume1)!
    typesToUse.insert(forcedExpiratoryVolume1)
    let forcedVitalCapacity = HKObjectType.quantityType(forIdentifier: .forcedVitalCapacity)!
    typesToUse.insert(forcedVitalCapacity)
    let inhalerUsage = HKObjectType.quantityType(forIdentifier: .inhalerUsage)!
    typesToUse.insert(inhalerUsage)
    let insulinDelivery = HKObjectType.quantityType(forIdentifier: .insulinDelivery)!
    typesToUse.insert(insulinDelivery)
    let numberOfTimesFallen = HKObjectType.quantityType(forIdentifier: .numberOfTimesFallen)!
    typesToUse.insert(numberOfTimesFallen)
    let peakExpiratoryFlowRate = HKObjectType.quantityType(forIdentifier: .peakExpiratoryFlowRate)!
    typesToUse.insert(peakExpiratoryFlowRate)
    let peripheralPerfusionIndex = HKObjectType.quantityType(forIdentifier: .peripheralPerfusionIndex)!
    typesToUse.insert(peripheralPerfusionIndex)
    
    // Activity
    let stepCount = HKObjectType.quantityType(forIdentifier: .stepCount)!
    typesToUse.insert(stepCount)
    let distanceWalkingRunning = HKObjectType.quantityType(forIdentifier: .distanceWalkingRunning)!
    typesToUse.insert(distanceWalkingRunning)
    let distanceCycling = HKObjectType.quantityType(forIdentifier: .distanceCycling)!
    typesToUse.insert(distanceCycling)
    let pushCount = HKObjectType.quantityType(forIdentifier: .pushCount)!
    typesToUse.insert(pushCount)
    let distanceWheelchair = HKObjectType.quantityType(forIdentifier: .distanceWheelchair)!
    typesToUse.insert(distanceWheelchair)
    let swimmingStrokeCount = HKObjectType.quantityType(forIdentifier: .swimmingStrokeCount)!
    typesToUse.insert(swimmingStrokeCount)
    let distanceSwimming = HKObjectType.quantityType(forIdentifier: .distanceSwimming)!
    typesToUse.insert(distanceSwimming)
    let distanceDownhillSnowSports = HKObjectType.quantityType(forIdentifier: .distanceDownhillSnowSports)!
    typesToUse.insert(distanceDownhillSnowSports)
    let basalEnergyBurned = HKObjectType.quantityType(forIdentifier: .basalEnergyBurned)!
    typesToUse.insert(basalEnergyBurned)
    let activeEnergyBurned = HKObjectType.quantityType(forIdentifier: .activeEnergyBurned)!
    typesToUse.insert(activeEnergyBurned)
    let flightsClimbed = HKObjectType.quantityType(forIdentifier: .flightsClimbed)!
    typesToUse.insert(flightsClimbed)
    let nikeFuel = HKObjectType.quantityType(forIdentifier: .nikeFuel)!
    typesToUse.insert(nikeFuel)
    let appleExerciseTime = HKObjectType.quantityType(forIdentifier: .appleExerciseTime)!
    typesToUse.insert(appleExerciseTime)
    if #available(iOS 13.0, *) { // apple stand time is only availabe in iOS 13
        let appleStandTime = HKObjectType.quantityType(forIdentifier: .appleStandTime)!
        typesToUse.insert(appleStandTime)
    }
    
    // Reproductive Health
    let basalBodyTemperature = HKObjectType.quantityType(forIdentifier: .basalBodyTemperature)!
    typesToUse.insert(basalBodyTemperature)
    let uvExposure = HKObjectType.quantityType(forIdentifier: .uvExposure)!
    typesToUse.insert(uvExposure)
    

    
    return typesToUse
}

