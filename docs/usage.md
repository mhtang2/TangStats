---
layout: TangStats
title: Usage
---
# **Very important** usage information

# Packet formatting
- **This must be correct, or the program may not work**
- Packet formatting following HFT XII format. Check examplepacket.pdf for an example
- Be sure to include author of questions in < angled brackets > after each tossup and bonus. 
	- Even if there is no author, including a pair of empty brackets <> is recommended. 
- If you choose to include category data, add them in this format {Category, Subcategory} or {Category} after the < author data >

- Add question number followed by a period and a space before each tossup and bonus.
	- e.g. "1. This author of..."
	- _Highly recommend **not** bolding the question number and period_. It may lead to inconsistencies 
- Add "[10]" before each bonus part, and add "ANSWER: " before each answer to a bonus part.
	- Please don't bold these either
- Make sure there are only two sections, with the tossup section preceding the bonus section.

# Logistics
- During data processing, player names and team names must be consistent over rounds and packets, otherwise they will be treated as different players/teams 
	- e.g. "matty t" in round 1 will be counted as a different player than "matty tang" from round 2. 
- It's a good idea for moderators to agree on a naming convention (first name last initial?). To make it less error prone, the program will automatically ignore letter-casing and non-alphanumeric characters 
	- e.g. "UNI...lab_ a" will automatically register as the same team as "Uni Lab A".
	- alternatively, distribute the teams.txt files to all moderators so this isn't an issue

# Compiling round stats into Tournament Summary
Please **do not edit** any part of the Excel files generated after each round! The program reads them to generate the Tournament Summary, so do not edit any Excel files until after you have compiled the Tournament Summary. Feel free to make a copy though and edit that as you like :)

To generate the Tournament Summary, send all the round files to one moderator (Google Drive?) and hit the "Total Summary Tool" button

# Program files
**license.bin**, **config.ini**, and **teams.txt** need to be kept in the same folder as the program

# Configuration
**license.bin** links the program to _your_ computer. **Don't lose or edit it!** or your program will require another key

**config.ini** decides the point values of Powers, Correct buzzes, and Negs. It has three lines:
```
power=15
correct=10
neg=-5
```
Feel free to change the numbers but do not reorder the lines.

**teams.txt** is included so team data does not have to be retyped every round. Each line has comma separated data for one team:
```
teamName, player1, player2, player3, player 4...
teamName2, player1, player2, player3, player 4...
Uni Lab A, Dylan, Ethan, Jonathan
```
You can share this file between moderators to make it easier
