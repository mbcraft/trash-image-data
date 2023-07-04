/**
Questa classe contiene i dati relativi a un'unica area taggata.
Contiene l'elenco dei punti e il tipo di tag (stringa).
*/

class TaggedArea implements Drawable {

  color COLOR_UNTAGGED = color(0,0,0);
  color COLOR_ALLUMINIO = color(184,184,184);
  color COLOR_PLASTICA = color(224,222,83);
  color COLOR_VETRO = color(39,205,0);
  color COLOR_CARCAR = color(228,234,234);
  color COLOR_INDIFFERENZIATA = color(229,72,223);
  color COLOR_INGOMBRANTI = color(213,67,67);
  color COLOR_ORGANICO = color(121,67,67);
  
  
  PointList myArea = new PointList();
  String tagType = "untagged";
  
  PointList getPointList() {
    return myArea;
  }
  
  String getTagType() {
    return tagType;
  }
  
  void setTagType(String type) {
    if (getPointList().isClosed()) {
      tagType = type;
      logInfo("Area taggata come : "+type);
    } else {
      logError("Errore : È necessario chiudere l'area per effettuare il tag.");
    }
  }
  
  void fromJson(JSONObject data) {
    tagType = data.getString("type");
    myArea.fromJson(data.getJSONArray("area"));
    myArea.markAsClosed();
  }
  
  JSONObject toJson() {
    JSONObject data = new JSONObject();
    data.setString("type",tagType);
    data.setJSONArray("area",myArea.toJson());
    return data;
  }
  
  void prepare() {
   if (tagType.equals("untagged")) {
     stroke(COLOR_UNTAGGED);
     strokeWeight(3);
     print("Color set : unknown/untagged\n");
   }
   if (tagType.equals("Alluminio")) {
     stroke(COLOR_ALLUMINIO);
     strokeWeight(3);
     print("Color set : alluminio\n");
   }
   if (tagType.equals("Carta/Cartone")) {
     stroke(COLOR_CARCAR);
     strokeWeight(3);
     print("Color set : carta/cartone\n");
   }
   if (tagType.equals("Vetro")) {
     stroke(COLOR_VETRO);
     strokeWeight(3);
     print("Color set : vetro\n");
   }
   if (tagType.equals("Plastica")) {
     stroke(COLOR_PLASTICA);
     strokeWeight(3);
     print("Color set : plastica\n");
   }
   if (tagType.equals("Organico")) {
     stroke(COLOR_ORGANICO);
     strokeWeight(3);
     print("Color set : organico\n");
   }
   if (tagType.equals("Indifferenziata")) {
     stroke(COLOR_INDIFFERENZIATA); 
     strokeWeight(3);
     print("Color set : indifferenziata\n");
   }
   if (tagType.equals("Rifiuti pesanti/ingombranti")) {
     stroke(COLOR_INGOMBRANTI);
     strokeWeight(3);
     print("Color set : rifiuti pesanti/ingombranti\n");
   }
   
  }
  
  void draw() {
    noFill();
    getPointList().draw();
    print("polyline drawed\n");
  }
    
}