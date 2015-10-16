import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.gicentre.utils.gui.TextPopup; 
import org.gicentre.utils.stat.*; 
import controlP5.*; 
import processing.serial.*; 
import java.io.*; 
import java.util.Arrays; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class WheeStat6_0d extends PApplet {

/*  WheeStat6_0d Processing sketch
 *    version d has offset removed
 *    version c was working fine with EX7 potentiostats
 *    8/13/15
 *   GUI for WheeStat 6 Potentiostats. 
 *    Compatible with WheeStat6a or later Energia Code
 *    
 *    by Jack Summers, Ben Hickman 
 *  
 *    Imports
 *    Classes
 */

///////////////////////////////////////// Imports///////////////////////////////
 // for warning window
    // For chart classes.
//import org.gicentre.utils.multisketch.*; // for integration window


                        // this is needed for BufferedWriter

/////////////////////////////////////////Classes////////////////////////////////
ControlP5 cp5, cp5b, cp5Com;
Serial serialPort;
Serial serial;
Textarea errorText;   // error text window
Textfield Starting_Voltage, End_Voltage, Scan_Rate, Delay_Time, fileName, readTime, readIvl;   //offset, Gain,
Textfield InitialV_Time, FinalV_Time, Number_of_Runs, Run_Interval, delay2;
DropdownList mode;    // defines the experiment to run
//////////////////////////////////variables/////////////////////////////////////
  boolean firstChart = true;
char[] strtochar;
String sData3;
boolean Modesel = false;
int overlay = 0;
String runMode;
/////////////////////////////
int LINE_FEED = 10; // used in serial read to identify data sets
String xChartLabel;

float[] xData;    
float[] yData;   

float xRead = 0;   
float yRead = 0;
float yRead1 = 0;
float yRead2 = 0;

int Ss;                          //Dropdown list returns float, convert into an int. 
String[] comList ;               //A string to hold the ports in.
//boolean serialSet;               //A value to test if we have setup the Serial port.
boolean Comselected = false;     //A value to test if you have chosen a port 
boolean gotparams = false;

boolean run = false;           // start run at bang
//boolean stopped = true;        // is program actually stopped?, added 6/17/14
//float p1;
//float p2;

//String ComP;           // cut for 6_0
//int serialPortNumber;   // cut for 6_0

//String[] file1 = new String[0];  // changed to array June 2015
String file2;                  // save file path

//String[] sData = new String[3];  //String sData;
String sData2 = " ";
char cData;
//char cData2;

//String Go = "1";
//int i =0;
int p = 0;           //stop signal

/************* parameters for retrieving values from text fields**********/
/*
String vInit;          
String vFinal;
String scanRate;
String initialDelay;
String nRuns;
String logIvl;


String del2;   // delay used in chronoamperometry?
*/
//String vOffset;
String cGain;
int iGain;  // ints used for converting values of gain and offset
//int iOffset;  // other textbox related ints removed for version 6.0
String runTxt;      // run or stop
// stuff for limit bar
int xBarPos = 700;
int yBarPos = 70;
int yBarSz = 400;
int yBarMin;
int yBarMax;
int bWidth = 15;   // bar width
float hiI = 0;
float lowI = 0;
int iHiI;
int iLowI;

PImage logo;
//////////////font variables////////////////////////////////////////////////////
PFont font = createFont("arial", 18);
PFont font2 = createFont("arial", 16);
PFont font3 = createFont("arial", 12); 
PFont font4 = createFont("andalus", 16);

////////////// stuff added to WheeStat 6//////////////////////////////////////
//ControlP5 cp5Com;
//Serial serial;
String comStatTxt = "not connected";
int iMod = 0;
String[] params = new String[11];
int legBoxX = 730;             // starting X and Y positions of legend boxes
int legBoxY = 300;   
int[] currentMax ={6600,2700,1700,1200,970,800,680,530,430,360,270,220,180,140,110,90,70,60,45,36,26,20,17,13,10,8,6,5,4,2,1};
String iLimit;
///////////// begun 6-08-15 /////////////////////////////
XYChart[] lineChart = new XYChart[10]; 
//     Textfield fileName;
float[] xMax = {  0};
float[] xMin = {  0};  
float[] yMax = {  0};  
float[] yMin = {   0};  
float chartXMax;
float chartYMax;
float chartXMin;
float chartYMin;

int[] red = {  255, 0, 0, 85, 0, 170, 0, 170, 85, 85}; //color parameters for data
int[] green = {  0, 0, 255, 0, 85, 85, 170, 0, 170, 85};
int[] blue = {  0, 255, 0, 170, 170, 0, 85, 85, 0, 85};

float xVal;
float yVal;

ArrayList xDataL = new ArrayList();
ArrayList yDataL = new ArrayList();
float[][] xRecover = new float[10][0];  
float[][] yRecover = new float[10][0];  
String[] sFileName = new String[10];
int runCount = 0;  // experiment count

boolean[] showChart = new boolean[10];
boolean[] hideChart = new boolean[10];
boolean[] selectBox = new boolean[10];

String[] file1 = new String[0];
String titles;
int selected = 0xff2FBEF5;    // color for selected box
//int bckgd = #AA000B;      // background color
int bkgnd = 0xff24375F;  // background color

String warnTxt = "no warning";
boolean warn = false;
String data;
boolean deleted = false;
int noDels = 0;     // number of deleted files
int fileCount;
String[] units = {"msec","sec","min"};
int iUnit = 0;

/////////////// setup //////////////////////////////////////////////////////////
public void setup()
{
  runButtonSetup();
  chartsSetup();
  textSetup();
  legendSetup();
  setupComPort();      // defined in Com_Port tab, added 5/31/15 to WheeStat 6
  connect();           // defined in Com_Port tab
//  frameRate(2000);  // is this necessary?
  logo = loadImage("LogoSMS.png");
  size(900, 550); 
}
///////////////////End Setup////////////////////////////////////////////////////


public void draw()
{
  if(run == true){
    fileCount = runCount;
  }else{
    fileCount = runCount-1;
  }
  background(bkgnd);  
  image(logo, 29, 500, 130, 34);
  fill(255);
  textAlign(LEFT);
  text(comStatTxt, 70, 25);  // displays com port state
  bar();                     // display limit bar
  stroke(255);
  noFill();
  rect (12, 50, 160, 85);
  rect (12, 143, 160, 85);
  rect (12, 236, 160, 85);
  rect (12, 330, 160, 85);

//  pushMatrix();
  textFont(font, 12);
  fill(0xffDEC507);
  textAlign(RIGHT);
  text("http://www.SmokyMtSci.com", 860, height-12);
//  popMatrix(); 
  textAlign(LEFT); 

  if (run == true) {
    runTxt = "Stop";
  } else { 
    runTxt = "Run";
  } 
  textFont(font, 24);
  fill(255);
  text(runTxt, 90, height-93);

//  pushMatrix();
  if (Modesel==false) {
    Starting_Voltage.hide();
    End_Voltage.hide();
    Delay_Time.hide();
    Scan_Rate.hide();
//    gain.hide();  // gain is slider, Gain is textfield
//    offset.hide();
    delay2.hide();
    Number_of_Runs.hide();
    Run_Interval.hide();
    readTime.hide();
    readIvl.hide();
  }
//  popMatrix(); 

  if (Modesel==true) {
    readGain();                       // new 8/13/15
    Starting_Voltage.show();  
    End_Voltage.show();
    Delay_Time.show();

//    gain.show();
//    offset.show();
    // rotation of text
    textFont(font2,16);
    fill(250, 250, 250);             //Chart heading color
//    textSize(16);
    text("Voltage limits (mV)", 20, 70);
    text("Current sensitivity", 20, 163);

    if (runMode=="ChronoAmp"||runMode=="ChronoAmp2") {   //chronoAmperometry experiments
      noFill();
      rect (730, 30, 160,140);
      fill(0xff36D8FF);
      rect(810,70,20,12);
      fill(0xff25A9AD);
      rect(830,70,20,12);
      fill(0xff255F13);
      rect(850,70,20,12);
      fill(255);
      textFont(font,16);
      text("msec", 815, 150);
      text(units[iUnit],815,100);
      textFont(font,16);
      text("Chronoamperometry",735,55);
      text("Delay 1", 20, 256);
      readTime.show();
      readIvl.show();
      delay2.hide();
      Scan_Rate.hide();
      Run_Interval.hide();
 //       text("Read interval (ms)", 20, 350);    
    } else if (runMode=="norm_Pulse") {
      readTime.hide();
      text("Delay 1     Pulse time", 20, 256);
      delay2.show();
      Scan_Rate.hide();
            Run_Interval.hide();
            readIvl.hide();
    } else {
      text("Delay 1   Scan Rate", 20, 256);
      Scan_Rate.show();
      delay2.hide();
      Run_Interval.hide();
      readTime.hide();
      readIvl.hide();
    }
    if (runMode=="logASV"||runMode == "CV_REP") {
      Number_of_Runs.show();
      Run_Interval.show();
      text("Multiple Runs", 20, 350);
    } else {
      Number_of_Runs.hide();
//      Run_Interval.hide();
    }
  }
  textFont(font2);
//////////// chart setup /////////////////
/*  chartXMax = max(xMax);
  chartYMax = max(yMax);
  chartXMin = min(xMin);
  chartYMin = min(yMin);

  fill(#EADFC9);               // background color
  int chartPosX = 200;        // position of background rectangle
  int chartPosY = 70;
  int chartSzX = 475;         // size of background rectangle
  int chartSzY = 450;
  translate(chartPosX,chartPosY);
//  rect(200, 70, 475, 450);    // chart background 
  rect(0, 0, chartSzX, chartSzY);    // chart background 
  fill(0, 0, 0);
  int posX = 20; //220;  // x position for center of y axis
  int posY = chartSzY/2; //260;  // y position for center of y axis
  translate(posX, posY);
  rotate(3.14159*3/2);
  textAlign(CENTER);
  text("Current  (microamps)", 0, 0);
  rotate(3.14159/2);        // return orientation and location
  translate(-posX, -posY);
translate(-chartPosX,-chartPosY);  

  if (runMode=="ChronoAmp"||runMode=="ChronoAmp2") { 
    xChartLabel = "Time (milliseconds)";
  } else {
    xChartLabel = "Voltage (mV)";
  }
  
  posX = 475;
  posY = 515;
  translate(posX, posY);
  textAlign(CENTER);
  text(xChartLabel, 0, 0);
  translate(-posX, -posY);  
*/
  displayCharts();  // defined in LineChart tab
  if (run==true && comList.length == 0) {
    run = false;
    Comselected = false;
    //    myTextarea2.setText("No COM");
    println("comm not connected");
  }
} 
/*
Buttons tab, WheeStat6_0 GUI sketch
  save and delete buttons are initiallized in legendSetup() routine
      -- found in legend tab
  saveRun() -- saves selected files to disk in response to save button,
            -- opens file directory
  delete() -- deletes selected files
  hide()  -- toggles selected files between hiden and displayed
*/

public void saveRun() {

  int[] select = new int[0];       // create list of files to save
  int[] fileLength = new int[0];    // lengths of data files
  titles = ",";

  for (int b = 0; b<10; b++) {
    if (selectBox[b] == true) {
      select = append(select, b);
      print("file appended: "+b);
      int c = xRecover[b].length;
      fileLength = append(fileLength, c);
      print(", length = "+c);
      String title = sFileName[b];
      println(", name = "+title);
      titles += title;
      titles += ",,";
    }
  }

  int maxLength = max(fileLength);

  println("maximum file length = "+maxLength);
  println(titles);


  String[] file3 = new String[2];

  file3[0] = titles;  
  file3[1] ="x data,y data";
  for (int e = 1; e<select.length; e++) {
    file3[1] += ",x data, y data";
  }
  println(file3[1]);
  file1=file3;       // program freaks if you try to re-initialize file 1

  //////////////// append data to file1
  for (int k = 0; k<maxLength; k++) {    // k = datum number

      data = "";                 // reset data file
      for (int n = 0; n<10; n++) {           // n = file number
         int m = xRecover[n].length;
      if (selectBox[n] == true) {
        if (m > k+1) {

          data += str(xRecover[n][k]);
          data += ",";
          data += str(yRecover[n][k]);
          data += ",";
        } 
        else {
          data += ",,";
        }
      }  // end of selected == true loop
    }  // end of n loop
    println(data);
    file1 = append(file1, data);
  }  
  println("end of k loop");
//  println(file1);

   selectOutput("Select a file to save:", "fileSelected");  // from WheeStat code
   
}

public void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {

    file2 = selection.getAbsolutePath();
    println("User selected " + file2);
    try {
      println("second time arround");
  //    println(file1);
      //  saveStream(file2,file1);
      saveStrings(file2, file1);
    }
    catch(Exception e) {
      println("problem in saveStream");
    }
  }
}


public void delete() {
  warn = false; 
println("file,  SelectBox,  sFileName, showChart");  
       for(int t = 0; t<10; t++){
println(t+",  "+ selectBox[t]+",  "+ sFileName[t]+",  "+ showChart[t]);
     }
          //////////// ///
  for (int b = 9; b>=0; b--) {

    if (selectBox[b] == true) {
      noDels += 1;
      println("file deleted: "+b);
      runCount -= 1;
      println("Run count decremented, run count = " +runCount);
      
      for (int c = b; c<runCount; c++) {
        println("changing values for file "+c);
        xMax[c] = xMax[c+1];
        yMax[c] = yMax[c+1];
        xMin[c] = xMin[c+1];
        yMin[c] = yMin[c+1];
        selectBox[c] = selectBox[c+1];
        sFileName[c] = sFileName[c+1];
        showChart[c] = showChart[c+1];
        hideChart[c] = hideChart[c+1];  // added 8-11-15
      }
      showChart[runCount+1] = false;    // noticed that runCount decremented above
      selectBox[runCount+1] = false;
      hideChart[runCount+1] = false;
      sFileName[runCount+1] = "";

      xMax[runCount+1] = 0;
      yMax[runCount+1] = 0;
      xMin[runCount+1] = 0;
      yMin[runCount+1] = 0; 

      xDataL.remove(b);         // remove data from array lists
     yDataL.remove(b);
 //////////// remove file b, re-order remaining files /////////////////
 for (int c = b; c < 10; c++){
  xRecover[c] = xRecover[c+1];
  yRecover[c] = yRecover[c+1];
 }
 //////////// end remove file b /////////////
      println(" through delete loop");
    }
  
    else {
    print("file retained: "+b);
    }
    println(" through loop");
      }
           for (int d = 0; d<10; d++) {
      println("Show Chart "+d+" = "+showChart[d]); 
     }
println("file,  SelectBox,  sFileName, showChart");
for(int t = 0; t<10; t++){
println(t+","+ selectBox[t]+","+ sFileName[t]+","+ showChart[t]);

     }
}

public void hide(){

 for (int b = 0; b<10; b++){
   if(selectBox[b] == true){
     if (run == true){
       fileCount = runCount;
     }else{
       fileCount = runCount-1;  //-1;
     }
//   showChart[fileCount - b] = !showChart[fileCount-b];  // was fileCount-b
  // hideChart[fileCount - b] = !hideChart[fileCount - b];
     
   showChart[b] = !showChart[b];
   hideChart[b] = !hideChart[b];
//   print("Chart hide "+b);
  // println(hideChart[b]);
   }
 } 
 println("runCount = "+runCount + ", fileCount = "+ fileCount);
 println("file  sFileName  selectBox  showChart  hideChart");
 for (int c = 0; c< runCount; c++){
   println(c+",  "+ sFileName[c]+ ", "+  selectBox[c]+",  "+  showChart[c]+",  "+  hideChart[c]); 
 }
 println("file  xMin  xMax  yMin   yMin");
 for (int j = 0; j<=runCount; j++){
   println(j+"  "+xMin[j]+ "  "+ xMax[j] + "  "+yMin[j]+"  "+yMax[j]);
   
 }
}
// Com_Port tab
// code on this tab sets up communications with the launchpad

public void setupComPort() {


  /////////  connect button ////////////
  cp5Com = new ControlP5(this); 
  cp5Com.addButton("connect")
    .setPosition(10, 10)
      .setSize(45, 20)
        //   .setImages(imgs)
        //    .updateSize()
        ;
}



/////////// connect button program //////////

public void connect() {
  println("connect button pressed");
  try {
       serialPort.clear();
       serialPort.stop();
  }
    catch(Exception e) {}
  char cData = 'a';
  comList = null;
  comList = Serial.list();  
  int n = comList.length;
  println("com list length = "+n);
  if (n == 0) { 
    comStatTxt = "No com ports detected";

}
  else {
    int k = 9999;
//    comStatTxt = "Multiple com ports detected";
    for (int m = 0; m <= n-1; m++) {
      try {
      serialPort = new Serial(this, comList[m], 9600);
      serialPort.write('*');         // should this be *?  was &
      // listen for return character '&'
      delay(100);
      if (serialPort.available () <= 0) {
        println (comList[m]+" not responsive");
      }
      if (serialPort.available() > 0)
      {
        cData =  serialPort.readChar();
        if (cData == '&') {
          println (comList[m]+" responsive");
          k = m;
        }else {
          println("Com port says: "+cData);
        }
        serialPort.clear();
        serialPort.stop();
      }
    }                       //  end of try loop
          catch(Exception e) {

      print(comList[m]);
      println(" not responsive");
    }    /// end of catch thing ///////////////

    }  // end of itterative look at ports
    if (k == 9999) {
      comStatTxt = "No responsive port";
    } else {
      serialPort = new Serial(this, comList[k], 9600); 
      comStatTxt = "Connected on "+comList[k];
      Comselected = true;
    }
  }
}
//Controllers tab
// controllers setup
// file name text field moved to legend tab

public void textSetup(){
 ////////////////////////////////////////////////Text Fields//////////////////////////////
  cp5 = new ControlP5(this);  
  PFont font = createFont("arial", 20);
  PFont font2 = createFont("arial", 16);
//  PFont font3 = createFont("arial",12); 

  Starting_Voltage = cp5.addTextfield("Starting_Voltage")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6)//(#FFFEFC) 
        .setColorForeground(0xffAA8A16) 
         .setPosition(20, 80)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                     .setText("-400")//;
  //                    controlP5.Label svl = Starting_Voltage
                  .setCaptionLabel("Initial")
                  .setFont(font2);
              //    .toUpperCase(false);
  //                      svl.setFont(font2);
  //                        svl.toUpperCase(false);
   //                         svl.setText("Initial");
  ;

  
  End_Voltage = cp5.addTextfield("End_Voltage")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(100, 80)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("400")
                  .setCaptionLabel("Final")
                  .setFont(font2);
;
       /*               controlP5.Label evl = End_Voltage.captionLabel(); 
                        evl.setFont(font2);
                          evl.toUpperCase(false);
                            evl.setText("Final");*/
  ;

  Scan_Rate = cp5.addTextfield("Scan_Rate")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(100, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("100")
                   .setCaptionLabel("mV/sec")
                  .setFont(font2);
;
                 /*     controlP5.Label srl = Scan_Rate.captionLabel(); 
                        srl.setFont(font2);
                          srl.toUpperCase(false);
                           srl.setText("mV/sec");*/
   ;
   delay2 = cp5.addTextfield("delay2")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(100, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("500")
                   .setCaptionLabel("mSec")
                  .setFont(font2);
;
/*                      controlP5.Label dl2 = delay2.captionLabel(); 
                        dl2.setFont(font2);
                          dl2.toUpperCase(false);
                           dl2.setText("mSec");*/
   ;
    cp5.addSlider("gain")
     .setPosition(20,212)
     .setSize(125,12)
     .setRange(0,30)
     .setValue(10)
     ;
//  cp5.getController("gain").getCaptionLabel().align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).setPaddingX(0); //BOTTOM_OUT  / 
  
    
    readTime = cp5.addTextfield("readTime")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
           .setPosition(750, 80) 
            .setSize(55, 25)
              .setFont(font)
                .setFocus(false)
                   .setText("500")
                  .setCaptionLabel("readTime")
                  .setFont(font2);
;
/*             controlP5.Label gain = Gain.captionLabel(); 
                        gain.setFont(font2);
                          gain.toUpperCase(false);
                            gain.setText("Gain"); */
  ;
  readIvl = cp5.addTextfield("readIvl")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16)  //position next
           .setPosition(750, 130)       

            .setSize(55, 25)
              .setFont(font)
                .setFocus(false)
                   .setText("20")
                  .setCaptionLabel("Read Interval")
                  .setFont(font2);
;  
        /*              controlP5.Label oLb = offset.captionLabel(); 
                        oLb.setFont(font2);
                          oLb.toUpperCase(false);
                            oLb.setText("Offset");
  ;*/

  Delay_Time = cp5.addTextfield("Delay_Time")
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(20, 266)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                    .setText("2")
                   .setCaptionLabel("Seconds")
                  .setFont(font2);
;
   /*                   controlP5.Label dtl = Delay_Time.captionLabel(); 
                        dtl.setFont(font2);
                          dtl.toUpperCase(false);
                            dtl.setText("Seconds");                    
  ;*/

  
    Number_of_Runs = cp5.addTextfield("Number_of_Runs")  // time based txt field
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(20, 360)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                    .setText("3")
                  .setCaptionLabel("Number")
                  .setFont(font2);
/*;
                      controlP5.Label norl = Number_of_Runs.captionLabel(); 
                        norl.setFont(font2);
                          norl.toUpperCase(false);
                            norl.setText("Number");                    
  ;*/
  
    Run_Interval = cp5.addTextfield("Run_Interval")  // time based txt field
    .setColor(0xff030302) 
      .setColorBackground(0xffCEC6C6) 
        .setColorForeground(0xffAA8A16) 
          .setPosition(100, 360)
            .setSize(60, 30)
              .setFont(font)
                .setFocus(false)
                   .setText("1")
                  .setCaptionLabel("Delay")
                  .setFont(font2);
/*;
                      controlP5.Label ril = Run_Interval.captionLabel(); 
                        ril.setFont(font2);
                          ril.toUpperCase(false);
                            ril.setText("D2 (Sec)");                    
  ;*/

  ///////////////////////////////////////text area//////////////////////////

  errorText = cp5.addTextarea("txt")  // save path text area
    .setPosition(350, 5) // was 280,5
      .setSize(240, 45)
        .setFont(font)      // was font 4
          .setLineHeight(20)
            .setColor(0xffFF9100)        //(#D60202)
              .setColorBackground(0xff24375F)         //(#CEC6C6)
                .setColorForeground(0xffAA8A16)//#CEC6C6
                    ;  

/* myTextarea2 = cp5.addTextarea("txt2")  // status and com port text area
    .setPosition(150, 8)
      .setSize(100, 20)   //was 30
        .setFont(createFont("arial", 12)) //(font)
          .setLineHeight(10)
            .setColor(#030302)
              .setColorBackground(#CEC6C6)
                .setColorForeground(#AA8A16)//#CEC6C6
                    ;*/

// cp5 = new ControlP5(this);

/*PImage[] imgs = {loadImage("run_button1.png"),loadImage("run_button2.png"),loadImage("run_button3.png")};
//PImage[] img2 = {loadImage("stop_button1.png"),loadImage("stop_button2.png"),loadImage("stop_button3.png")};
  cp5.addButton("play")
     .setValue(128)
     .setPosition(50,450)
     .setImages(imgs)
     .updateSize()
     ;*/


/******************* end cp5_controllers-setup ***********************/
 /////////////////////////////////////////Dropdownlist//////////////////////////
/*
ovrLy = cp5.addDropdownList("list-3", 200, 60, 80, 64)  // last digit was 84
    .setBackgroundColor(color(200))
      .setItemHeight(20)
          .setBarHeight(20)
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                ;
  ovrLy.captionLabel().set("No_Overlay");
  ovrLy.captionLabel().style().marginTop = 3;
  ovrLy.captionLabel().style().marginLeft = 3;
  ovrLy.valueLabel().style().marginTop = 3;
  ovrLy.setScrollbarWidth(10);

  ovrLy.addItem("no_overlay",0);
  ovrLy.addItem("overlay", 1);*/

 ///////////// mode dropdown list /////////////////////////////
  mode = cp5.addDropdownList("list-2", 260, 30, 80, 184)  // last digit was 124
    .setBackgroundColor(color(200))
      .setItemHeight(20)
          .setBarHeight(20)
          .setColorBackground(color(60))
            .setColorActive(color(255, 128))
              .setUpdate(true)
                                 .setCaptionLabel("Select Mode")
                                 .setId(1)
  //                .setFont(font2);
// ;
//  mode.captionLabel().set("Select Mode");
//  mode.captionLabel().style().marginTop = 3;
  //mode.captionLabel().style().marginLeft = 3;
//  mode.valueLabel().style().marginTop = 3;
  .setScrollbarWidth(10);

  mode.addItem("RAMP",0);
  mode.addItem("CV", 1);

  mode.addItem("ASV", 2);
  mode.addItem("logASV",3);
  mode.addItem("dif_Pulse", 4);
  mode.addItem("ChronoAmp",5);
  mode.addItem("ChronoAmp2",6);
  mode.addItem("norm_Pulse",7);
  mode.addItem("CV_REP",8);
}

/////////////////////////////////////////////////group programs/////////////////////////////////

public void controlEvent(ControlEvent theEvent) {
  if (theEvent.isGroup()) 
  {
  /*  if (theEvent.name().equals("list-1")) {

      float S = theEvent.group().value();
      Ss = int(S);
      Comselected = true;
    } */
   int Id = theEvent.getId();
   if (Id == 1){
 //   if (theEvent.name().equals("list-2")) {          // name() ia a problem
      float Mod = theEvent.getGroup().getValue();          // group() and value() are both problems
      int Modi = PApplet.parseInt(Mod);
      iMod = PApplet.parseInt(Mod);
      String [][] Modetype = mode.getListBoxItems(); 
      //Modetorun = Modetype[Modi][Modi];
      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
      Modesel = true;
      println("mode to run = "+runMode);
//      println("iMod int = "+iMod);
    }
    if (theEvent.getName().equals("list-3")) {
      float ovr = theEvent.getGroup().getValue(); 
      overlay = PApplet.parseInt(ovr);
    }
  }
}
/* LineChart tab, WheeStat6_0 GUI sketch
 chartsSetup() -- initiallizes charts
 --called in setup loop
 displayCharts() -- sets up and displays charts
 -- called in draw loop
 setLimits() -- sets limits on x and y displays
 */

public void chartsSetup() {
  for (int y = 0; y<10; y++) {
    lineChart[y] = new XYChart(this);
    if (y == 0) {
      lineChart[y].showXAxis(true);
      lineChart[y].showYAxis(true);
    }
    lineChart[y].setPointColour(color(red[y], green[y], blue[y]));
    lineChart[y].setPointSize(5);
    lineChart[y].setLineWidth(2);
  }
}

public void displayCharts() {

  if (run == true) {            // copied from buttons tab
    fileCount = runCount;
  } else {
    fileCount = runCount-1;
  }

  //  boolean firstChart = true;  // moved to first tab
  for (int y = 1; y<10; y++) {
    if (showChart[y] == true) {
      firstChart = false;
    }
  }

  fill(0xffEADFC9);               // background color
  int chartPosX = 200;        // position of background rectangle
  int chartPosY = 70;
  int chartSzX = 475;         // size of background rectangle
  int chartSzY = 450;
  translate(chartPosX, chartPosY);
  //  rect(200, 70, 475, 450);    // chart background 
  rect(0, 0, chartSzX, chartSzY);    // chart background 
  fill(0, 0, 0);
  int posX = 20; //220;  // x position for center of y axis
  int posY = chartSzY/2; //260;  // y position for center of y axis
  translate(posX, posY);
  rotate(3.14159f*3/2);
  textAlign(CENTER);
  text("Current  (microamps)", 0, 0);
  rotate(3.14159f/2);        // return orientation and location
  translate(-posX, -posY);
  translate(-chartPosX, -chartPosY);  

  if (runMode=="ChronoAmp"||runMode=="ChronoAmp2") { 
    xChartLabel = "Time (milliseconds)";
  } else {
    xChartLabel = "Voltage (mV)";
  }

  posX = 475;
  posY = 515;
  translate(posX, posY);
  textAlign(CENTER);
  text(xChartLabel, 0, 0);
  translate(-posX, -posY);  
  ///////////////// end of chart setup //////////////////

  //////////// read data and graph it /////////////////
  if (run==true) {
    read_serial();
    lineChart[0].setData(xData, yData);
    if(runCount==0){
    chartXMax = xMax[0];
    chartYMax = yMax[0];
    chartXMin = xMin[0];
    chartYMin = yMin[0];
    }
    else{
    chartXMax = max(chartXMax, xMax[0]);
    chartYMax = max(chartYMax, yMax[0]);
    chartXMin = min(chartXMin, xMin[0]);
    chartYMin = min(chartYMin, yMin[0]);
     }
     for(int w = 0; w<runCount; w++){
       int x = runCount-w;                  // x decreases from runCount to 1
       int y = w+1;                         // y increases from 1 to runCount
       if(showChart[x] == true){
     setLimits(lineChart[x]);
     lineChart[x].setData(xRecover[w], yRecover[w]);  
     lineChart[x].draw(270,80,400,400);
     }
     }
         try {
           setLimits(lineChart[0]);
      lineChart[0].draw(250, 70, 430, 420);
    }
    catch(Exception e) {
      println("problem with drawing lineChart[0]");
    }
  }  
  ////////////////// end of "if run is true" loop ////////////////
else{               // start of "if run is false" loop
  if (run == false && runCount >0) { 
    lineChart[0].setData(xRecover[runCount-1], yRecover[runCount-1]);
  }


  ////////////////////// setup and display charts ///////////////
  boolean firstGraph = true;
  for (int b=0; b<10; b++) {

int h = b+1;
    if (showChart[b] == true) {     // determine axes max and mins

      if (firstGraph == true) {          // for first shown graph, set Max and Min values
        chartXMax = xMax[h]; 
        chartXMin = xMin[h]; 
        chartYMax = yMax[h]; 
        chartYMin = yMin[h]; 
        firstGraph = false;
      } else {
      
    chartXMax = max(chartXMax, xMax[h]);
    chartYMax = max(chartYMax, yMax[h]);
    chartXMin = min(chartXMin, xMin[h]);
    chartYMin = min(chartYMin, yMin[h]);

      }

    }  // end of loop for determining axes parameters
  }   // end of b loop    

  setLimits(lineChart[0]);
    try {
      lineChart[0].draw(250, 70, 430, 420);
    }
    catch(Exception e) {
      println("problem with drawing lineChart[0]");
    }

  for (int k = 0; k<fileCount; k++) {
    int p = fileCount - k;
    if (showChart[k] == true) {
                   // remember that chart[0] is sized different than other charts
         lineChart[p].setData(xRecover[k], yRecover[k]);  
                   // puts most recent chart in file 0, 1st chart in highest file number

       setLimits(lineChart[p]);
       lineChart[p].draw(270, 80, 400, 400);  // draw linechart[0] is below

      // line chart 0 params: 250, 70, 430,420
    }
  }   // end of k loop
} // end of "else" loop (run is false)
  for (int m = 0; m<runCount; m++) {
    if (showChart[m] == true) {
      legend(fileCount-m, (m)*20);         // display legend next to file name, defined in  legend tab
                                            //  need "fileCount-h to get colors correct (they change)
      if (selectBox[m] == true) {
        fill(selected);
        stroke(selected);
        rect(legBoxX+40, 20*m + legBoxY-5, 100, 20);    // selection box, 
        fill(255);
      }
    } // end of "if showChart m is true" loop
    if (showChart[m] == true || hideChart[m] == true) {    // chart can be shown, hidden, or not exist
      textAlign(LEFT);
      stroke(255);                    // added 8-12-15
      fill(255);
      text(sFileName[m], legBoxX+50, 20*m+legBoxY + 10);  // was 780, 130
    }
  }  // end of m loop
if(run == false){
}
  }
//}
public void setLimits(XYChart thing) {
  thing.setMaxX(chartXMax);
  thing.setMaxY(chartYMax);
  thing.setMinX(chartXMin);
  thing.setMinY(chartYMin);
}

// logData tab; WheeStat Processing sketch


public void logData( String fileName, String newData, boolean appendData)  
{
  BufferedWriter bw=null;
  try {                            //try to open the file
    FileWriter fw = new FileWriter(fileName, appendData);
    bw = new BufferedWriter(fw);
    bw.write(newData);
  } 
  catch (IOException e) {
  } 
  finally {
    if (bw != null) { //if file was opened try to close
      try {
        bw.close();
      } 
      catch (IOException e) {
      }
    }
  }
}
// SerialRead tab.  
// WheeStat5_4_2 GUI  SerialRead Tab
//  May 26, 2014-work on gain and offset adjustments to GUI
//  May 29, moved to Energia; lines 8,22, 27 changed
//  July 2014, work on limit bar

public void  read_serial() {
  float mVmin;
  float mVmax;  
//  println("serialRead line 10");
  if (serialPort.available () <= 0) {
  }
  if (serialPort.available() > 0) { 
    sData3 = serialPort.readStringUntil(LINE_FEED);  // new JS11/22
//println("serialRead line 14");
    if (sData3 != null && p != 0) {            //p = reset counter
      String[] tokens = sData3.split(",");
      tokens = trim(tokens);  
      if (run == true) {  
        if (runMode == "ASV" || runMode == "logASV" || runMode == "dif_Pulse") {
          xRead = Float.parseFloat(tokens[0]);  
          yRead1 = Float.parseFloat(tokens[1])/1000;  
          yRead2 = Float.parseFloat(tokens[2])/1000; 
          mVmin = Float.parseFloat(tokens[3]);
          mVmax = Float.parseFloat(tokens[4]);
          yRead = (yRead1 - yRead2);                  // sign change 10/16/2015
        } else {    // for RAMP and CV experiments
          xRead = Float.parseFloat(tokens[0]);  
          yRead = (Float.parseFloat(tokens[1]))/1000;  // had offset, gainK and factor of 1000
          mVmin = Float.parseFloat(tokens[2]);
          mVmax = Float.parseFloat(tokens[3]);
        }
        ///////////////////stuff for limit bar //////////////////
        if (yData.length == 1) {        
          lowI = mVmin;
          hiI = mVmax;
        }
        if (mVmin<= lowI) {
          lowI = mVmin;
        }
        if (mVmax>= hiI) {
          hiI = mVmax;
        }
    //////////////////////////end of run ///////////////
        if (xRead == 99999) {  // signals end of run
          //       if (xRead == 99999  && yRead == 99999) { // signals end of run
          run = false;    // stops program

          /////////////// added from charts sketch //////////
          xDataL.add (xData);             // enter data files to lists
          yDataL.add (yData);
          xRecover[runCount] = (float[])xDataL.get(runCount);   // get data from list
          yRecover[runCount] = (float[])yDataL.get(runCount);
          showChart[0] = true;
          
                    runCount += 1;
          xMax[runCount] = xMax[0];
          xMin[runCount] = xMin[0];
          yMax[runCount] = yMax[0];
          yMin[runCount] = yMin[0];
  


          println("runCount = "+runCount);
          selectBox[runCount-1]=true; 
          if (runCount > 1) {
            selectBox[runCount-2]=false;
          }

          ////////////// end of new stuff //////////////////

          println("end the madness");
          print("High value = ");
          println(iHiI);
          print("Low value = ");
          println(iLowI);
          gotparams = false;
          cData = 'a';   

          xRead = 0;  
          yRead = 0;
        }  // end of if xRead = 99999 
        ////////////////////  for log run ////////////////////
        else if (xRead == 55555)  // start of log run
        {
          println("new run");
        }
  
        else {  
          xData = append(xData, xRead);
          yData = append(yData, yRead);
          print("yRead = "+yRead);
          println(" data length = "+xData.length);
          xMax[0] = max(xData);
          xMin[0] = min(xData);
          yMax[0] = max(yData);
          yMin[0] = min(yData);
          //               logData(file1, sData3, true);
        }
      }
    }
    p +=1;
  } // end of if serial available > 0
}
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

public void legendSetup() {
    int active = 0xffEA4F2F;  // color selection for mouse over button
  cp5b = new ControlP5(this);

    cp5b.addButton("saveRun")
     .setPosition(legBoxX, legBoxY-90)    
     .setColorBackground(selected)
     .setColorCaptionLabel(0xff030302) 
     .setColorForeground(active)    
     .setSize(50, 20)
     .setCaptionLabel("Save Run")
     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
      ;
                    
  cp5b.addButton("delete")     
     .setPosition(legBoxX+70, legBoxY-90)
     .setColorBackground(selected)  
     .setColorCaptionLabel(0xff030302)
     .setColorForeground(active)   
     .setSize(50, 20)
     .setCaptionLabel("Delete")
     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
     ; 
     
 cp5b.addButton("hide")     
     .setPosition(legBoxX+20, legBoxY-120)
     .setColorBackground(selected)  
     .setColorCaptionLabel(0xff030302)
     .setColorForeground(active)   
     .setSize(70, 20)
     .setCaptionLabel("Hide / Show")
     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
     ; 
            
  fileName =  cp5b.addTextfield("fileName")  
     .setColor(0xff030302) 
     .setColorBackground(0xffCEC6C6) 
     .setColorForeground(0xffAA8A16) 
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
 char a = PApplet.parseChar(t+48);
 char b = PApplet.parseChar(u+48);
 println("runCount = "+runCount);
 sFileName[runCount] = sFileName[runCount] + "_" + a+b;
}

public void legend(int k, int off){        // displays box with line and dot
  translate(legBoxX,legBoxY+off);
       fill(0xffEADFC9);               // background color
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


 public void mousePressed(){

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
//limitBar tab in WheeSat5_4 Processing sketch
// begun 7/6/14 by JSS
// intent is to show relationship betwen obs current range and limits

public void bar() {

   iHiI = PApplet.parseInt(hiI);  //hiI and lowI set to zero on line 314, first tab
   iLowI = PApplet.parseInt(lowI);  // should be in mV at this point?    

  if(iLowI <=-1900 || iHiI >=2000) {
       errorText.setText("Scale Error: Decrease Gain"); 
  }
/*  
if(iHiI >=2000) {
  if(iLowI <= -1900) {
   errorText.setText("Scale Error: Decrease Gain");         //
   }
 else {
      errorText.setText("ERROR: I-max Too High, Decrease Offset");         //
 } 
}
  if(iLowI <=-1900 && iHiI <=2000) {
   errorText.setText("ERROR: I-min Too Low, Increase Offset");         //
   } */
  int hiVal = (2048-iHiI)*yBarSz/4096; //(1650-iHiI)*yBarSz/3300;// Bar drawn from top = 0 down
  int lowVal = (2048-iLowI)*yBarSz/4096;   
  translate(xBarPos,yBarPos);
for (int i = hiVal; i < lowVal; i++) {
float red;    // = 255-(300*exp(-0.01*i));
float green; //*(1/(1+i);
float blue;  // = 50-(0.5*i);
float rMax = 0.5f*yBarSz;  // posn of maximum green intensity 
float gMax = 0.5f*yBarSz;  // posn of maximum green intensity 
float bMax = 0.5f*yBarSz;  // posn of maximum green intensity 
float rS = 1/yBarSz;   // was 4.8 width parameter for red
float gS = 5.1f/yBarSz;   //width parameter for green
float bS = 4.9f/yBarSz;   // was 0.8 width parameter for blue
float rD = exp(rS*(i-rMax));
float gD = exp(gS*(i-gMax));
float bD = exp(bS*(i-bMax));
int rA = 800;     // maximum red intensity
int gA = 800;     // maximum green intensity
int bA = 900;     // maximum blue intensity
 red = rA*rD/sq(1+rD);
 green = gA*gD/sq(1+gD);
 blue = bA*bD/sq(1+bD);
  
  stroke(red, green, blue, 250);

  line(0, i, bWidth, i);
  
} 
 ////  Center and limits  /////

 stroke(255,255,255,250);
 line(0,0, bWidth,0);
 translate(0, 0.5f*yBarSz); 
 line(0,0, bWidth,0);
 translate(0, 0.5f*yBarSz);  
 line(0,0,bWidth,0);
 translate(0,-yBarSz);
 
 translate(-xBarPos, -yBarPos); 
 }
/* runButton tab, WheeStat6_0 GUI sketch
   runButtonSetup() -- initiallizes up run button
   
*/

public void runButtonSetup() {
  
//  int active = #EA4F2F;
  cp5 = new ControlP5(this);
  PImage[] imgs = {loadImage("run_button1.png"),loadImage("run_button2.png"),loadImage("run_button3.png")};
  cp5.addButton("Start_Run")
    .setColorBackground(0xffFFFEFC)//#FFFEFC 
        .setColorCaptionLabel(0xff030302) //#030302
          .setColorForeground(0xffAA8A16)
          .setColorActive(0xff06CB49)
            .setPosition(40, 430)
            .setImages(imgs)      // new
              .setSize(40, 40)
//                .setTriggerEvent(Bang.RELEASE)
                    .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)  
                      ;

}

public void Start_Run() {                // start run button- level 1
  if (run == true) {             // stop run
    serialPort.write('%');
    println("Run stopped");
    run = false;    // stops program
    println("end the madness");
    gotparams = false;
    cData = 'a';
  } 
  
  else {                           // beginning of start run loop - level 2
    ///////////// start new stuff /////////////
    showChart[runCount] = true;       
    println("run count = "+runCount);
    getFileName();
    println("file name ="+sFileName[runCount]);

    xMax = append(xMax, 0);
    yMax = append(yMax, 0);
    xMin = append(xMin, 0);
    yMin = append(yMin, 0);
  //////////////// end of new stuff ///////////
    run = true;
    if (run == true && Comselected == true)             
    {
      if (gotparams == false)   // added to update chart in real time Nov19 BH
      {
        hiI = 0;     // probably not needed
        lowI = 0;
 //       println("runButton tab line 21");
 //       if (yData.length != 0 && overlay == 0) {   // from Ben's-6/13/14
 //// new method to clear data files //// June 2015   
    xData = new float[0];              // clear xData and yData arrays between runs
    yData = new float[0];

          
 println("getting Params");
        getParams();    // get paramaters from text fields (text field programs)
println("got Params");
        //////// serialPort.write writes to microcontroller to begin run //////////////////
        delay(100);  // added from Ben's work on reset
        params[0] = String.valueOf(iMod);
//        String sMod = String.valueOf(iMod);
        serialPort.write("&");
        println("Start run");
        for (int h = 0; h<11; h++){         // number of parameters increased
         serialPort.write(params[h]);
        serialPort.write(',');
        delay(100);
      // println(params[h]); 
       }
       
        p=0;                    // reset counter for serial read
        println("begin run");   // shows up in bottom window

//  line below cut 6/8/15
   //     logData(file1, "", false);     // log data to file 1, do not append, start new file

          ////////read parameter input until LaunchPad transmits '&'/////////
        while (cData!='$') // && cData !='@')       // '&' character signifies parameters received
          // '@' character signifies ?? cut 6/3/15
          // '&' changed to '$'
        {         
          if (serialPort.available () <= 0) {
          }
          if (serialPort.available() > 0)
          {
            cData =  serialPort.readChar();     // cData is character read from serial comm. port
            sData2 = str(cData);            //sData2  is string of cData 
            // line below cut 6/8/15
//            logData(file1, sData2, true);   // at this point we are logging the parameters
            println(sData2);
            errorText.setText(""); 
            if (cData == '$')               //  Launchpad sends & char at end of serial write
            {
              println("parameters received");
              gotparams = true;
              // line below cut 6/8/15
//              logData(file1, "\r\n", true);  // added 6/13-from Ben, what does this do?
            }
          }
        }  // end of while loop with params
      } // end if gotparam == false   Nov 19 BH

      //////////// graph data //////////////////////////////////////////////

      //     read_serial();
    }  // end of "if run == true" loop
    /* if (xData.length>4 && xData.length==yData.length)
     {
     //      lineChart.setMaxX(max(xData));   //  changed to have 'subset', as below-why?
     lineChart.setMaxX(max(subset(xData, 1)));   
     lineChart.setMaxY(max(subset(yData, 1)));
     lineChart.setMinX(min(subset(xData, 1)));
     lineChart.setMinY(min(subset(yData, 1)));
     lineChart.setData(subset(xData, 1), subset(yData, 1));
     } // End of if (V.length stuff
     */
  }
}
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

public void readGain(){
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
    float fResol = limits/0.4095f;
    int rDig1 = PApplet.parseInt(fResol/10);
    int rDig2 = PApplet.parseInt(fResol%10);
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
    int fullScale = PApplet.parseInt(limits);
        iLimit = str(fullScale);
        iLimTxt = "+/- "+iLimit+" uA full scale";
      }   
    else {
      int fullScale = PApplet.parseInt(limits);
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "WheeStat6_0d" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
