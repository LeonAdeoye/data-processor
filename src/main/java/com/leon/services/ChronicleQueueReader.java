package com.leon.services;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


// Using it offers low latency, durable interprocess communication (IPC), and no Garbage Collection overhead.
public class ChronicleQueueReader
{
	private Chronicle chronicle;

	public ChronicleQueueReader() throws IOException
	{
		/*There are three concepts characteristic of a Chronicle Queue:
		Excerpt – is a data container
		Appender – appender is used for writing data
		Tailer – is used for sequentially reading data
		We'll reserve the portion of memory for read-write operations using the Chronicle interface. */

		File queueDir = Files.createTempDirectory("chronicle-queue").toFile();
		chronicle = ChronicleQueueBuilder.indexed(queueDir).build();

		//We will need a base directory where the queue will persist records in memory-mapped files.
		// ChronicleQueueBuilder class provides different types of queues.
		// In this case, we used IndexedChronicleQueue, which uses the sequential index to maintain memory offsets of records in a queue.
	}

	public void read() throws IOException
	{
		// Reading the values from the queue can easily be done using the ExcerptTrailer instance.
		// It is just like an iterator we use to traverse a collection in Java.
		ExcerptTailer tailer = chronicle.createTailer();
		while (tailer.nextIndex()) {
			tailer.readUTF();
			tailer.readInt();
			tailer.readLong();
			tailer.readDouble();
		}
		// After creating the trailer, we use the nextIndex method to check if there is a new excerpt to read.
		// Once ExcerptTailer has a new Excerpt to read, we can read messages from it using a range of read methods for literal and object-type values.
		// Finally, we finish the reading with the finish API.
		tailer.finish();
		tailer.close();
		chronicle.close();
	}

	public void write() throws IOException
	{
		// To write the items to a queue, we'll need to create an object of ExcerptAppender class using the Chronicle instance.
		//After creating the appender, we will start the appender using a startExcerpt method.
		// It starts an Excerpt with the default message capacity of 128K. We can use an overloaded version of startExcerpt to provide a custom capacity.
		//Once started, we can write any literal or object value to the queue using a wide range of write methods provided by the library.
		//Finally, when we're done with writing, we'll finish the excerpt, save the data to a queue, and later to the disc.
		ExcerptAppender appender = chronicle.createAppender();
		appender.startExcerpt();

		appender.writeUTF("Hello World");
		appender.writeInt(101);
		appender.writeLong(System.currentTimeMillis());
		appender.writeDouble(90.00192091d);
		appender.finish();
	}
}
