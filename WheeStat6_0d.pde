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
import org.gicentre.utils.gui.TextPopup; // for warning window
import org.gicentre.utils.stat.*;    // For chart classes.
//import org.gicentre.utils.multisketch.*; // for integration window
import controlP5.*;
import processing.serial.*;
import java.io.*;                        // this is needed for BufferedWriter
import java.util.Arrays;
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
int selected = #2FBEF5;    // color for selected box
//int bckgd = #AA000B;      // background color
int bkgnd = #24375F;  // background color

String warnTxt = "no warning";
boolean warn = false;
String data;
boolean deleted = false;
int noDels = 0;     // number of deleted files
int fileCount;
String[] units = {"msec","sec","min"};
int iUnit = 0;

/////////////// setup //////////////////////////////////////////////////////////
void setup()
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


void draw()
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
  fill(#DEC507);
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
      fill(#36D8FF);
      rect(810,70,20,12);
      fill(#25A9AD);
      rect(830,70,20,12);
      fill(#255F13);
      rect(850,70,20,12);
      fill(255);
      textFont(font,16);
      text("msec", 800, 150);
      text(units[iUnit],800,100);
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
