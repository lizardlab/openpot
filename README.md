# OpenPot
OpenPot is a replacement to the Instant Pot Smart Bluetooth Low Energy app that was taken off the App and Play Markets. OpenPot tries to mimic as closely as possible the layout (with some minor improvements) of the original app. It supports all cooking function, telemetry reporting (temperature, pressure level, heating, etc.), timer adjustment, and changing all the parameters for doing cooking. The only thing this version does not do is the "recipe mode" which involves a state machine and I felt was too complicated for the amount of time I wanted to spend on this project.

## Using OpenPot
Open the main page and click the blue "Connect" button. When the top icon turns white. Then you can select any of the functions and start your Instant Pot wirelessly! This app should be compatible with the newest version of Android, which is version 16 at the time of this writing.

## BLE Protocol
To make this as "future proof" as possible the reverse engineered protocol is detailed below. The 2 main packets we are concerned with are both 20-byte long, and one handles operating the Instant Pot "command packet" while the other gives details on how the cook is coming along "telemetry packet". Also for full customization, there is a time service which the Timer 1, Timer 2, 24 hour clock, and time setting can be both obtained and written to.

### UUIDs
*Note: for 16 bit UUIDs you use the full UUID of 0000XXXX-0000-1000-8000-00805F9B34FB where the short version replaces the XXXX*
| Description                  | UUID   | Direction  |
|------------------------------|--------|------------|
| Control Service              | 0xdab0 | Service    |
| Control Characteristic       | 0xdab1 | Write      |
| Telemetry Characteristic     | 0xdab2 | Notify     |
| Time Service                 | 0xdaa0 | Service    |
| Clock Characteristic         | 0xdaa1 | Read/Write |
| Timer 1 Characteristic       | 0xdaa2 | Read/Write |
| Timer 2 Characteristic       | 0xdaa3 | Read/Write |
| 24 hour clock Characteristic | 0xdaa4 | Read/Write |

### Command Packet
```
| Byte | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 |
|      |     PREAMBLE      | CK | TS | MD | UNK|   TA    | DR |                  UNK                  | CK |
```
| Name     | Description         | Format                                                                                             |
|----------|---------------------|----------------------------------------------------------------------------------------------------|
| PREAMBLE | Fixed start message | `aa555a01`                                                                                         |
| CK       | Cook Mode           | 1 byte (see table)                                                                                 |
| TS       | Timer Selection     | No timer - 0x20, Timer 1 - 0x11, Timer 2 - 0x12                                                    |
| MD       | Mode                | Normal - 0xf0, Less - 0xb0, More - 0x70. (Yogurt) Pasteurize - 0xc0, Yogurt - 0x40, Ferment - 0x80 |
| TA       | Timer Amount        | 4 bytes HH:MM in BCD, 0x0000 for no timer (yogurt/saute have no delay)                             |
| DR       | Duration Amount     | 4 bytes HH:MM in BCD                                                                               |
| CK       | Check Code          | Add previous bytes together XOR with 255 + 1                                                       |

#### Cook Mode
| Mode       | Value |
|------------|-------|
| Rice       | 0x01  |
| Multigrain | 0x02  |
| Porridge   | 0x03  |
| Steam      | 0x04  |
| Yogurt     | 0x05  |
| Poultry    | 0x07  |
| Chili      | 0x08  |
| Meat/Stew  | 0x09  |
| Soup       | 0x0a  |
| Manual     | 0x0c  |
| Keep Warm  | 0x0d  |

This packet needs to be written (with no response aka "command mode") to 0xdab1.

### Telemetry Packet
```
| Byte | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 |
|      |     PREAMBLE      | UNK| WM |        UNK        |    RT   | TP | HL |          UNK           | CK |
```
| Name     | Description         | Format                                       |
|----------|---------------------|----------------------------------------------|
| PREAMBLE | Fixed start message | `aa554002`                                   |
| WM       | Work Mode           | 1 byte (see table)                           |
| RT       | Remaining Time      | 4 bytes HH:MM in BCD                         |
| HL       | Heating Level       | 1 Byte % formula: value / 16 * 100           |
| CK       | Check Code          | Add previous bytes together XOR with 255 + 1 |

#### Work Mode
| Name  | Value   |
|-------|---------|
| On    | 0xc     |
| Warm  | 0xe/0xd |
| Timer | 0xb     |
| Off   | *       |

### Time
The time is a 32 bit (big endian) timestamp with a custom epoch of 2001-01-01T00:00:00 in the current timezone. Example would be 2024-10-17T12:00:00 would become `B025C12C`. Can be written and read to 0xdaa1.

The 24 hr is a simple 1 bit value. It is 1 if it is 24 hour time, 0 if it is 12 hour time. Write/Read this value to 0xdaa4

Timers are similarly HH:MM in BCD format. They are also supplied in the command packet. Timer 1 is 0xdaa2 and Timer 2 is 0xdaa3 and both can be written and read.

## Dedication
This app is dedicated to [Lizard Company](https://lizard.company) it was a company that I founded 20 years ago on this day. Lizard Company specializes in creating end-to-end technology solutions for our clients. Happy 20th birthday Lizard Company!
