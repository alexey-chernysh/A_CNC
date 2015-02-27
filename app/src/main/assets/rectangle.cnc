G92 X0 Y0 (MSG Set current position to null)
G91 (MSG Relative coordinate mode)
G00 X5.0 Y5.0 (MSG Move to perforation point)
G41
M07
G01 Y55.0
G01 X50.0
G01 Y-50.0
G01 X-55.0
M08
G40
M02
