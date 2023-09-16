// Define a global variable to store the original ticks table
ticks: ([] date:(); timestamp:(); sym:(); qty:(); px:());

// Define a function to insert random data into the table
insert_random_data: {
  dates: `date$2018.01.01 + 10000000?31;
  timestamps: `datetime$dates + 10000000?24:00:00:0000;
  qtys: 100 * 1 + 10000000?100;
  ixs: 10000000?3;
  syms: `aapl`amzn`googl ixs;
  pxs: (1 + 10000000?.03) * 176.0 141.0 135.0 ixs;

  newRow: ([] date:dates; timestamp:timestamps; sym:syms; qty:qtys; px:pxs);

  // Append the new data to the 'ticks' table
  ticks,: newRow
}

// Schedule the insert_random_data function to run every 1 second
.z.ts:{ insert_random_data[]; }





