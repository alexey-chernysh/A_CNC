G92 X0 Y0 (MSG Set current position to null)
G91 (MSG Relative coordinate mode)
G00 X120.0 Y90.0 (MSG Move to perforation point)
G41 (Set cutter compensation offset right)
M07 (Start cutting with perforation)
G03 X5.0 Y5.0 J5.0 ; cut on input arc
G03 I-25.0 ; cut hole
G03 X-5.0 Y5.0 I-5.0 ; cut output arc
M08 ; turn torch off
G40 ; compensation off
G00 X-95.0 Y-85.0
G41
M07
G01 Y5.0
G01 Y25.0
G01 X51.0 Y68.000
G02 X48.0 I24.0 J-18.0
G01 X51.0 Y-68.0
G01 Y-25.0
G01 X-155.0
M08
G40
M02
