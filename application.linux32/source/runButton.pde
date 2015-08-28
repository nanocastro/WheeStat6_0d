/* runButton tab, WheeStat6_0 GUI sketch
   runButtonSetup() -- initiallizes up run button
   
*/

void runButtonSetup() {
  
//  int active = #EA4F2F;
  cp5 = new ControlP5(this);
  PImage[] imgs = {loadImage("run_button1.png"),loadImage("run_button2.png"),loadImage("run_button3.png")};
  cp5.addButton("Start_Run")
    .setColorBackground(#FFFEFC)//#FFFEFC 
        .setColorCaptionLabel(#030302) //#030302
          .setColorForeground(#AA8A16)
          .setColorActive(#06CB49)
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
