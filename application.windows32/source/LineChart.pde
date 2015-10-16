/* LineChart tab, WheeStat6_0 GUI sketch
 chartsSetup() -- initiallizes charts
 --called in setup loop
 displayCharts() -- sets up and displays charts
 -- called in draw loop
 setLimits() -- sets limits on x and y displays
 */

void chartsSetup() {
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

void displayCharts() {

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

  fill(#EADFC9);               // background color
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
  rotate(3.14159*3/2);
  textAlign(CENTER);
  text("Current  (microamps)", 0, 0);
  rotate(3.14159/2);        // return orientation and location
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
void setLimits(XYChart thing) {
  thing.setMaxX(chartXMax);
  thing.setMaxY(chartYMax);
  thing.setMinX(chartXMin);
  thing.setMinY(chartYMin);
}

