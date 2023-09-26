// To run this script in C:\q\w64> (The double quotes are necessary because of the space in the path name)
// q "c:\Users\Leon Adeoye\development\data-processor\create_table_wo_ts.q" -p 5001

dates: 2018.01.01 + 10000000?31
// This line generates a list of random dates. It starts from January 1, 2018, and adds a random number of days between 0 and 30 to generate dates within the month of January 2018.

times: 10000000?24:00:00:0000
// This line generates a list of random times of day, ranging from midnight (00:00:00.0000) to just before midnight (23:59:59.9999).

qtys: 100 * 1 + 10000000?100
// This line generates a list of random quantities (qtys). Each quantity is generated by multiplying a random integer between 1 and 100 by 100.

ixs: 10000000?3
// This line generates a list of random integers, each between 0 and 2, inclusive.

syms: `aapl`amzn`googl ixs 
// his line creates a list of symbols representing stock symbols (aapl, amzn, and googl) based on the values generated in ixs.
// The list of symbols will be determined by the random integers generated in ixs`.

pxs: (1 + 10000000?.03) * 176.0 141.0 135.0 ixs
// This line generates a list of random prices (pxs).
// It multiplies a random floating-point number between 1.0 and 1.03 (inclusive) by one of the three specified prices (176.0, 141.0, or 135.0) based on the values generated in ixs.


ticks: ([] date:dates; time:times; sym:syms; qty:qtys; px:pxs)

// This line creates a table named ticks with columns for date, time, symbol, quantity, and price.
// The data for each column is populated using the lists generated earlier.


ticks: `time xasc ticks
// This line sorts the ticks table first by the date column and then by the time column in ascending order, effectively ordering the tick data chronologically.

