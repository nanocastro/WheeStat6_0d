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
      int Modi = int(Mod);
      iMod = int(Mod);
      String [][] Modetype = mode.getListBoxItems(); 
      //Modetorun = Modetype[Modi][Modi];
      runMode = Modetype[Modi][0]; // replaced earlier line in newer sketch?
      Modesel = true;
      println("mode to run = "+runMode);
//      println("iMod int = "+iMod);
    }
    if (theEvent.getName().equals("list-3")) {
      float ovr = theEvent.getGroup().getValue(); 
      overlay = int(ovr);
    }
  }
}
