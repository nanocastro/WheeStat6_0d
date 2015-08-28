// SerialRead tab.  
// WheeStat5_4_2 GUI  SerialRead Tab
//  May 26, 2014-work on gain and offset adjustments to GUI
//  May 29, moved to Energia; lines 8,22, 27 changed
//  July 2014, work on limit bar

void  read_serial() {
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
          yRead = (yRead2 - yRead1);
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
