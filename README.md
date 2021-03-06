# Gavin Gilbert: COP4520_Dechev - Assignment 3

## Foreword
This assignment sucked. Royally. I tried to get it to work in Rust, but I just couldn't. Had to go back in time a couple of years and break out my Java chops, but I think it turned out pretty good in the end. 

Here's hoping I didn't leave a bug to cost me 25% of my grade this time lol.

## What You Need
You'll need to have a valid install of Java to compile and run this code. I'm using openjdk version 11.0.14.1.

`sudo apt update`

`java -version` will check if you have a current install of Java on your device.

`javac -version` will check for a version of the compiler specifically. (This is important because I've had an install of java, but not javac before)

If either of these are missing, run `sudo apt remove default-jre` and then `sudo apt install default-jre`. This should wipe any artifacts of old versions you had before and install the latest version.

Run `java -version` and `javac -version` one more time just to make sure your install is valid.

After that, navigate to the root directory of the repo, and go to src/.

Run `javac *.java` to compile all the files, and then `java Problem1` or `java Problem2` to execute the respective program.

## Problem 1 - The Method
This problem uses a lock-free linked-list to simulate this "chain of presents" idea that Dechev has crafted for us. The majority of the linked-list code was taken from the textbook and modified to function in this context.

The use of an `ArrayList` which can be generated easily and then shuffled for "randomness" saved a lot of time and effort compared to ensuring that a unique random number is generated by each thread. This was one of the few ideas I carried over from my initial attempt at a Rust implementation. Runtime will vary due to the servants randomly deciding to add/remove/check for gifts in the chain. My system averages between 1 and 2 seconds.

## Problem 1 - Efficiency
I think that approx. 2 seconds for 500,000 items (time may vary depending on how RNJesus feels in that moment) is more than fair. Especially considering that the list has opportunity to grow very quickly (should RNJesus decide to add everything before starting to remove) which will make list traversal very slow, I think this is a very good solution.

## Problem 2 - The Method
This problem uses a lock-free linked-list to keep track of sensor readings in an ordered list. This linked-list implementation is a near copy of the first problem's, except that it's explicitly changed to work with doubles instead of integers. I used Thread.sleep() to simulate waiting a minute. I have it set to 1 (ms) by default so you don't have to wait forever, but this can be changed in `Sensor.java` under the constant `WAIT_TIME`. You can also adjust the `Sensor` class's `HOURS_TO_RUN` value to increase or decrease the number of hourly reports to generate.

I have one of the threads acting as a "Lead Sensor", which means that it will be the one to generate the hourly report. However, it still takes readings and adds them to the linked list, just like the other sensors.

## Problem 2 - Efficiency
This runs very quickly on my hardware, easily under half-a-second. However, this is with the minimum wait period of 1ms between simulated readings and 5 hourly reports total. Runtime will obviously vary greatly once you start messing with those 2 variables.

## Correctness
Because we are using `AtomicMarkableReferences` for each Node in the linked lists, there's no need to worry about correctness problems.

Specifically in Problem 1, we're also using a static `AtomicInteger` to keep count of how many letters we've written, which ensures correctness for the count, on top of the operations done on the list itself.

As for Problem 2, by nature of us using an ordered list, we can simply look at the first 5 elements for the lowest temperatures, and the last 5 elements for the highest temperatures. Generally, we should expect each node to finish adding its readings to the list before the report is generated by the lead to ensure that all measurements are present in the list when the report process starts. If we ever run into issues with correctness, we can simply up the wait time to give the sensors more time to submit results before the reporting process starts

The code is lock-free, so there is no risk of deadlock, since if the `add()` or `remove()` fails, the thread will simply move on to something else. On top of that, there will always be work being done. This is true even in the case where an `add()` or `remove()` for any given thread fails.