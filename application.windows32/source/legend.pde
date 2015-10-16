/*
legend tab, WheeStat6_0
  legendSetup() -- initializes saveRun and delete buttons and fileName textbox
                -- called in setup loop
  getFileName() -- gets file name from fileName textbox--called early in runButton tab
  legend() -- chart legend shows names and data point colors for each data set
           -- called in main tab, ~line 353
  mousePressed() -- selects or deselects data files based on mouse click
  
global variables legBoxX and legBoxY must be initialized in main tab
These are ints that determine where on the screen the legend will show
*/

void legendSetup() {
    int active = #EA4F2F;  // color selection for mouse over button
  cp5b = new ControlP5(this);

    cp5b.addButton("saveRun")
     .setPosition(legBoxX, legBoxY-90)    
     .setColorBackground(selected)
     .setColorCaptionLabel(#030302) 
     .setColorForeground(active)    
     .setSize(50, 20)
     .setCaptionLabel("Save Run")
     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
      ;
                    
  cp5b.addButton("delete")     
     .setPosition(legBoxX+70, legBoxY-90)
     .setColorBackground(selected)  
     .setColorCaptionLabel(#030302)
     .setColorForeground(active)   
     .setSize(50, 20)
     .setCaptionLabel("Delete")
     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
     ; 
     
 cp5b.addButton("hide")     
     .setPosition(legBoxX+20, legBoxY-120)
     .setColorBackground(selected)  
     .setColorCaptionLabel(#030302)
     .setColorForeground(active)   
     .setSize(70, 20)
     .setCaptionLabel("Hide / Show")
     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
     ; 
            
  fileName =  cp5b.addTextfield("fileName")  
     .setColor(#030302) 
     .setColorBackground(#CEC6C6) 
     .setColorForeground(#AA8A16) 
     .setPosition(legBoxX, legBoxY-50)
     .setSize(90, 20)
     .setFont(font2)
     .setFocus(false)
     .setText("file"); 
}

public void getFileName(){
  println("in getFileName");
  sFileName[runCount] = cp5b.get(Textfield.class,"fileName").getText();   
                              // get file name from textfield.
 int s = runCount + noDels;
 int t = s/10;
 int u = s%10;
 char a = char(t+48);
 char b = char(u+48);
 println("runCount = "+runCount);
 sFileName[runCount] = sFileName[runCount] + "_" + a+b;
}

void legend(int k, int off){        // displays box with line and dot
  translate(legBoxX,legBoxY+off);
       fill(#EADFC9);               // background color
       rect(0,0, 30, 14);       
       stroke(100);
       strokeWeight(2); 
       line(5, 7,25,7);          
       stroke(red[k],green[k],blue[k]); 
       fill(red[k],green[k],blue[k]);
       ellipseMode(CENTER);
       ellipse(15,7,3,3);         
       strokeWeight(1);
       stroke(0);  // return stroke and fill colors to defaults
       fill(255);
   translate(-legBoxX,-legBoxY-off);
}


 void mousePressed(){

 for (int q = 0; q<runCount; q++){
   int maxY = 20*q + legBoxY+14;          
   int minY = 20*q + legBoxY;
if (mouseX > legBoxX && mouseX <legBoxX+140 && mouseY > minY && mouseY < maxY){  
  selectBox[q] =! selectBox[q];
  print("file "+q);
  println(" selected, state = "+selectBox[q]);
}

 }
 for (int r = 0; r<runCount; r++) {
  if (selectBox[r] == true) {
   print("Select box = "+r);
  println(" is true."); 
  }

  else {
  print("Select box = "+r);
  println(" is false."); 
  }
 }
 for (int s = 0; s<3; s++){
  if (mouseX > 20*s+810 && mouseX <20*s+830 && mouseY > 70 && mouseY < 82){ 
    iUnit = s;
  }
 }
 }
