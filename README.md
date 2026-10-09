# FFMAwt

A FFM wrapper around Native AWT

## Goal of this project

My goal is to have a graphics api where glue code can be written in java, such
as `windows.h` or `x11`, while mac requires some native code due to
objective-c.

If this api can be constructed, it means that the glue code, could be service
loaded in and a graphics api bound through java FFM could be potentially
utilized without any native code.
