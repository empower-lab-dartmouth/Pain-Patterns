import pymysql
import pandas as pd
import time


'''
Return a list of column names a table that the pymysql cursor is pointing
to.
'''


def get_column_names(cursor):
    desc = cursor.description
    column_names = []
    for tup in desc:
        column_names.append(tup[0])
    return column_names


'''
Returns a list of the table names stored in the SQL.
'''


def get_table_names(hostname, username, password, database_name):
    pymysql.install_as_MySQLdb()
    connection = pymysql.connect(hostname, username, password, database_name)

    cursor = connection.cursor()
    cursor.execute("SELECT * FROM " + table_name)

    data = cursor.fetchall()
    data_list = []


'''
Access data from remote SQL database (the example credentials use
an AWARE backend database) using the provided credentials, then
returns a pandas dataframe consisting of the data from SQL table
table_name.
'''


def gen_df_from_remote_SQL(hostname, username, password, database_name, table_name):
    pymysql.install_as_MySQLdb()
    connection = pymysql.connect(hostname, username, password, database_name)

    cursor = connection.cursor()
    cursor.execute("SELECT * FROM " + table_name)

    data = cursor.fetchall()
    data_list = []

    column_names = get_column_names(cursor)

    # Read rows from data into a list from which we can create the dataframe
    for row in data:
        data_list.append(row)

    df = pd.DataFrame(data_list)
    df.columns = column_names

    # Convert epoch timestamps to readable dates/times
    df['timestamp'] = df['timestamp'].apply(lambda ts: time.strftime(
        '%Y-%m-%d %H:%M:%S', time.localtime(ts / 1000)))

    return df


if __name__ == "__main__":

    # Sample credentials for a sample study that I setup on AWARE.
    # Credentials can be accessed on api.awareframework.com from
    # Study Dashboard > View Credentials.
    # Column names can be viewed by accessing
    hostname = "api.awareframework.com"
    username = "Ren_2425"
    password = "4kjUaIyK"  # password redacted
    database_name = "Ren_2425"

    table_name = "accelerometer"  # Table of interest

    print(gen_df_from_remote_SQL(hostname, username,
                                 password, database_name, table_name))
