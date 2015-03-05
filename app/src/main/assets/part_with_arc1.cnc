G92 X0 Y0 (MSG Set current position to null)
G91 (MSG Relative coordinate mode)
G00 X5.0 Y5.0 (MSG Move to perforation point)
G41
M07
G01 Y105.0
G01 X30.0
G01 Y10.0
G02 X40.0 I20.0
G01 Y-10.0
G01 X30.0
G01 Y-100.0
G01 X-30.0
G01 Y10.0
G03 X-40.0 I-20.0
G01 Y-10.0
G01 X-40.0
M08
G40
M02
