/******************* text field programs tab ***********************/

public void getParams() {  

/********************************* 
mode = params[0]  vOffset      = 5
vInit    = 1      initialDelay = 6
vFinal   = 2      nRuns        = 7
scanRate = 3      logIvl       = 8
cGain    = 4      del2         = 9
************************************/

  /* Info from text fields transmitted to u-controller as strings
   * to convert to float, int, or char, use commands below:
   *  float fSpeed = float(stringSpeed);
   *  iSpeed = round(fSpeed);
   *  cSpeed = char(iSpeed);
  */ 

  params[1] = cp5.get(Textfield.class, "Starting_Voltage").getText();
  params[2] = cp5.get(Textfield.class, "End_Voltage").getText();
  params[3] = cp5.get(Textfield.class, "Scan_Rate").getText();
        println("to getParams[3]");
  float fGain = cp5.get(Slider.class, "gain").getValue();
//   cGain = cp5.get(Textfield.class, "Gain").getText();
    iGain = round(fGain);       
    if (iGain <= -1) {
      iGain = 0;
    }
    if (iGain >= 31) {
      iGain = 30;
    }
/*  cGain = cp5.get(Textfield.class, "Gain").getText();
    iGain = round(float(cGain));       
    if (iGain <= -1) {
      iGain = 0;
    }
    if (iGain >= 31) {
      iGain = 30;
    }  */
      println("to getParams[4]");
  params[4]  = nf(iGain, 3);   // pad with zeros to 3 digits, changed from 6 digits
  /*vOffset = cp5.get(Textfield.class, "offset").getText();
    iOffset = round(float(vOffset))+165; //512;
  params[5] = nf(iOffset, 6);   // pad with zeros to 6 digits
  */
  params[5] = "165";   // was current offset
  params[6] = cp5.get(Textfield.class, "Delay_Time").getText();
  params[7] = cp5.get(Textfield.class, "Number_of_Runs").getText();
  println("got to params[7]");
      if (runMode=="ChronoAmp"||runMode=="ChronoAmp2") { 
        println("into ChronoAmp stuff"); 
  params[8] = cp5.get(Textfield.class, "readIvl").getText();
  println("read Ivl seems to work");
  params[9] = cp5.get(Textfield.class, "readTime").getText();


      }
      else {
  params[8] = cp5.get(Textfield.class, "Run_Interval").getText();
  params[9] = cp5.get(Textfield.class, "delay2").getText();
      }
  String sUnit = str(iUnit);
  println("sUnit = "+sUnit);
  params[10] = sUnit;     // units of readTime (ms, sec, or min)
        println("got to end of params[10]");
}

void readGain(){
   float fGain = cp5.get(Slider.class, "gain").getValue();
//   cGain = cp5.get(Textfield.class, "Gain").getText();
    iGain = round(fGain);       
    if (iGain <= -1) {
      iGain = 0;
    }
    if (iGain >= 31) {
      iGain = 30;
    }
    float limits = currentMax[iGain];
    ///////////////////////////// digital resolution ///////////////
    float fResol = limits/0.4095;
    int rDig1 = int(fResol/10);
    int rDig2 = int(fResol%10);
    String resolution;
    String iLimTxt;
    if (rDig1 < 3){
    resolution = str(rDig1)+"."+str(rDig2)+" nA resolution";
    }
    else if (rDig1 >=3 && rDig1 <30){
    resolution = str(rDig1)+" nA resolution";
    }
    else if (rDig1 >=30 && rDig1 <300){
    resolution = str(rDig1/10)+"0 nA resolution";
    }
    else if (rDig1 >=300 && rDig1 <3000){
    resolution = str(rDig1/100)+"00 nA resolution";
    }
    else {
    resolution = str(rDig1/1000)+" uA resolution";
    }
    ///////////////////// full scale text ////////////
  if (limits<30){
    int fullScale = int(limits);
        iLimit = str(fullScale);
        iLimTxt = "+/- "+iLimit+" uA full scale";
      }   
    else {
      int fullScale = int(limits);
        iLimit = str(fullScale/10);
        iLimTxt = "+/- "+iLimit+"0 uA full scale";
      }
    
    
      textAlign(LEFT);
    textFont(font, 16);
    stroke(255);
    fill(255);
    text(iLimTxt, 20, 185);    //650,50);
    textFont(font, 14);
//    text("full scale",80,200);
    text(resolution,40,205);
}
