knowledge-is-power
==================
KiP is an educational AMI kit for smart grid technologies

### Introduction
KiP consists of two components.

The first component is a hardware board which measures power quality information. It takes non-intrusive measurements and sends that information over IP using UDP to the KiP software for analysis. The board was designed to be built cheaply (less than $20) and also with simplicity in mind.

The second component is the KiP software which receives measurements from the board and calculates voltage, watts, and amperage. The data can be displayed in real time and can also be stored for later analysis.

In the future, we plan to include a real time database in order to complete our AMI infrastructure. We also plan to include a system allowing the [WattDepot](https://code.google.com/p/wattdepot/) system to read measurements from our database.

### More information
For more information, please read our [F.A.Q.](https://github.com/anthonyjchriste/knowledge-is-power/wiki/F.A.Q.) and visit our [wiki](https://github.com/anthonyjchriste/knowledge-is-power/wiki).
