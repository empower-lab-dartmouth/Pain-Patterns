from read_sql_remote_data import gen_df_from_remote_SQL
from read_csv_data import read_csv_data
import numpy
import csv
import matplotlib.pyplot as plt


# analyze plugin_ios_activity_recognition data from AWARE server SQL database.
def activitiesAnalysis(df):
    print(df.to_string())
    plt.close('all')
    gdf = df.groupby("activities")  # group by activities type for aggregate
    actMap = {}
    # create a map that links activities type to frequency
    for key, item in gdf:
        actMap[key] = len(gdf.get_group(key))
    labels = list(actMap.keys())
    sizes = list(actMap.values())
    fig1, ax1 = plt.subplots()
    ax1.pie(sizes, labels=labels, autopct='%1.1f%%', startangle=90)
    # Equal aspect ratio ensures that pie is drawn as a circle.
    ax1.axis('equal')
    plt.show()

#write df to a csv
def write_df_to_csv(df, name):
    dfcsv = df.to_csv()
    f = open(name+'.csv', 'w')
    f.write(dfcsv)
    f.close()
    
#writes healthkit quantity data from AWARE server SQL database into a csv.
def healthQuanAnalysis(df):
    print("running healthQuanAnalysis()")
    #hdf = df.groupby(["timestamp"])
    
    dfcsv = df.to_csv()
    f = open('healthQuanttest.csv', 'w')
    f.write(dfcsv)
    f.close()


# analyze healthkit category data from AWARE server SQL database.
def healthCatAnalysis(df):
    print("running healthCatAnalysis()")
    #hdf = df.groupby(["timestamp"])
    
    dfcsv = df.to_csv()
    f = open('healthCattest.csv', 'w')
    f.write(dfcsv)
    f.close()
    print(df.to_string())


# analyze qualtrics survey data
def qualtricsAnalysis(df): 
    print("running qualtricsAnalysis()")
    print(df)


#analyze accelerometer data
def misAnalysis(df):
    print("running misAnalysis")
    dfcsv = df.to_csv()
    f = open('healthCattest.csv', 'w')
    f.write(dfcsv)
    f.close()
    print(df.to_string())


if __name__ == "__main__":
    # credentials for PPS setup for AWARE.
    # Credentials can be accessed on api.awareframework.com
    hostname = "api.awareframework.com"
    username = "Ren_2425"
    password = "4kjUaIyK"
    database_name = "Ren_2425"

#     devicesdf = gen_df_from_remote_SQL(hostname, username,
#                                    password, database_name, "aware_device")
#     iosactdf = gen_df_from_remote_SQL(hostname, username,
#                                    password, database_name, "plugin_ios_activity_recognition")
#     misdf = gen_df_from_remote_SQL(hostname, username,
#                                    password, database_name, "accelerometer")
#     healquandf = gen_df_from_remote_SQL(hostname, username,
#                                         password, database_name, "health_kit_quantity")
#     healcatdf = gen_df_from_remote_SQL(hostname, username,
#                                        password, database_name, "health_kit_category")
   
    
    # qualdf = read_csv_data('')
    #make dict to easily pull each df
    names = ["plugin_ios_activity_recognition", "health_kit_quantity", "gravity",
             "accelerometer","rotation","linear_accelerometer","gyroscope","barometer",
             "magnetometer", "battery", "health_kit_category", "sensor_wifi", "proximity",
             "screen", "timezone", "network", "locations", "aware_device", "bluetooth"]
    df_map = {}
    for name in names:
#         print(name)
        df_map[name] = gen_df_from_remote_SQL(hostname, username, password, database_name, name)
#     write_df_to_csv(df_map["health_kit_quantity"], "health_kit_quantity")
#     write_df_to_csv(df_map["health_kit_category"], "health_kit_category")
    write_df_to_csv(df_map["accelerometer"], 'accelerometer')
