import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class trash extends PApplet {


// constants --------------------

//image and tags files paths

String IMAGE_PATH_PREFIX = "samples/untagged/dscn";
String IMAGE_PATH_SUFFIX = ".jpg";

String TAG_DATA_PATH_PREFIX = "samples/data/tags_";
String TAG_DATA_PATH_SUFFIX = ".json";

//image id

int MIN_IMAGE_ID = 1;
int MAX_IMAGE_ID = 2693;
int IMAGE_ID_DIGITS = 4;

//image display size

int IMG_DISPLAY_WIDTH = 800;
int IMG_DISPLAY_HEIGHT = 600;

// -------------------------------

//global variables

int imageId = 1;
PImage currentImage;
String lastMessage = "";
int messageColor = 0;
TaggedImage currentTags = new TaggedImage();

public void logInfo(String message) {
  lastMessage = message;
  messageColor = 0xff000000;
}

public void logError(String error) {
  lastMessage = error;
  messageColor = 0xffff2222;
}

public void printGenericHelp() {
  print("\n\n\n\n-------------------------------------------------------\n\n");
  print("INSTRUZIONI : ");
  print("");
  print("Utilizza il pulsante sinistro del mouse per delimitare i contorni di un oggetto.\n");
  print("Chiudi l'area col pulsante destro.\n");
  print("Utilizza Backspace per eliminare l'ultima area delimitata.\n");
  print("Utilizza la barra spaziatrice per ridisegnare la finestra.\n");
  print("Utilizza i pulsanti 'a', 'v', 'c', 'i', 'o', 'p', 't' per indicare il tipo di materiale delimitato\n");
  print("(rispettivamente 'Alluminio', 'Vetro', 'Carta/Cartone', 'Indifferenziata', 'Organico', \n");
  print("  'Plastica' e 'meTalli o cemenTi') nell'ultima area delimitata.\n");
  print("Utilizza le freccette destra e sinistra per scorrere le immagini, e \n");
  print("freccia su e freccia giu' per caricare e salvare i dati in file JSON.\n");
  print("Tasti . e - per scorrere di +10 e -10 le immagini.\n");
  print("Tasti PageUp e PageDown per scorrere di +100 e -100 le immagini.\n");
  print("Premi 'h' per visualizzare l'help relativo ai tag.");
  print("-------------------------------------------------------\n\n");
}

public void printTagHelp() {
  print("\n-------------------------------------\n");
  print("TIPOLOGIE DI TAG DISPONIBILI :\n\n");
  print("a : Alluminio\n");
  print("v : Vetro\n");
  print("c : Carta/Cartone\n");
  print("i : Indifferenziata\n");
  print("o : Organico\n");
  print("p : Plastica\n");
  print("t : Metalli e Cementi (Rifiuti ingombranti)\n\n");
  print("\n-------------------------------------\n");
}

public void setup() {
  

  printGenericHelp();

  reloadImage();
}

public String getCurrentNormalizedImageId() {
  String num = ""+imageId;
  while (num.length()<IMAGE_ID_DIGITS) {
    num = "0"+num;
  }
  return num;
}

public void reloadImage() {
  String path = IMAGE_PATH_PREFIX+getCurrentNormalizedImageId()+IMAGE_PATH_SUFFIX;
  currentImage = loadImage(path);
  currentTags = new TaggedImage();
  currentTags.setupWithImage(currentImage);
  currentTags.setImagePath(path);
  logInfo("Image loaded : "+path);
}

public void draw () {
  stroke(0,0,0);
  imageMode(CORNER);
  image(currentImage, 0, 0, IMG_DISPLAY_WIDTH, IMG_DISPLAY_HEIGHT);
  fill(255);
  stroke(255);
  rect(0,IMG_DISPLAY_HEIGHT,IMG_DISPLAY_WIDTH,IMG_DISPLAY_HEIGHT+20);
  fill(0);
  textSize(18);
  color(messageColor);
  text(lastMessage,10,IMG_DISPLAY_HEIGHT+22);
  
  currentTags.prepare();
  currentTags.draw();
  
  //add draw of TaggedImage
  noLoop();
}


public void mouseClicked() {
  if (mouseButton==LEFT) {
    TaggedArea ta = currentTags.getLastTaggedArea();
    if (ta.getPointList().isClosed()) {
      if (ta.getTagType()=="untagged") {
        logError("\u00c8 necessario taggare l'area prima di proseguire.");
        return; 
      } else {
        currentTags.newTaggedArea();
        ta = currentTags.getLastTaggedArea();
      }
    }
    ta.getPointList().add((float)mouseX,(float)mouseY);
    loop();
  }
  if (mouseButton==RIGHT) {
    if (!currentTags.getLastTaggedArea().getPointList().isClosed()) {
      currentTags.getLastTaggedArea().getPointList().close();
      logInfo("Tag area closed.");
      loop();
    }
  }
}

public void keyPressed() {

  if (keyCode==BACKSPACE) {
    currentTags.deleteLastTaggedArea();
    loop();
  }
  
  if (key==' ') {
    loop();
  }
  
  //select tag key codes
  
  if (key=='u') {
    //untagged/unknown
    currentTags.getLastTaggedArea().setTagType("untagged");
    loop();
  }
  
  if (key=='a') {
    //alluminio
    currentTags.getLastTaggedArea().setTagType("Alluminio");
    loop();
  }
  if (key=='c') {
    //carta/cartone
    currentTags.getLastTaggedArea().setTagType("Carta/Cartone");
    loop();
  }
  if (key=='v') {
    //vetro
    currentTags.getLastTaggedArea().setTagType("Vetro");
    loop();
  }
  if (key=='p') {
    //plastica
    currentTags.getLastTaggedArea().setTagType("Plastica");
    loop();
  }
  if (key=='o') {
    //organico
    currentTags.getLastTaggedArea().setTagType("Organico");
    loop();
  }
  if (key=='i') {
    //indifferenziata
    currentTags.getLastTaggedArea().setTagType("Indifferenziata");
    loop();
  }
  if (key=='t') {
    //rifiuti pesanti o di grandi dimensioni (elettrodomestici, parti in cemento armato, ecc)
    currentTags.getLastTaggedArea().setTagType("Rifiuti pesanti/ingombranti");
    loop();
  }
  
  //help
  
  if (key=='h') {
    printTagHelp();
  }
  
  //load and save key codes
  
  if (keyCode==UP) {
    //carica i dati da file
    String path = TAG_DATA_PATH_PREFIX+getCurrentNormalizedImageId()+TAG_DATA_PATH_SUFFIX;
    JSONObject data = loadJSONObject(path);
    currentTags = new TaggedImage();
    currentTags.fromJson(data);
    currentTags.afterLoad(IMG_DISPLAY_WIDTH,IMG_DISPLAY_HEIGHT);
    logInfo("Loaded tag data from "+path);
    loop();
  }
  
  if (keyCode==DOWN) {
    //salva i dati su file
    String path = TAG_DATA_PATH_PREFIX+getCurrentNormalizedImageId()+TAG_DATA_PATH_SUFFIX;
    currentTags.beforeSave(IMG_DISPLAY_WIDTH,IMG_DISPLAY_HEIGHT);
    JSONObject data = currentTags.toJson();
    currentTags.afterLoad(IMG_DISPLAY_WIDTH,IMG_DISPLAY_HEIGHT);
    saveJSONObject(data,path);
    logInfo("Saved tag data to "+path);
    loop();
  }

  //select image key codes 
  
  if (keyCode==RIGHT) {
    if (imageId<MAX_IMAGE_ID-1)
    imageId++;
    reloadImage();
    loop();
  }
  
  if (keyCode==LEFT) {
    if (imageId>MIN_IMAGE_ID)
      imageId--;
    reloadImage();
    loop();
  }
  
  if (key=='.') { //MINUS : image - 10
    if (imageId>MIN_IMAGE_ID+9)
      imageId-=10;
    reloadImage();
    loop();
  }
  if (key=='-') { //DOT : image + 10 
    if (imageId<MAX_IMAGE_ID-9)
      imageId+=10;
    reloadImage();
    loop();
  }
  
  if (keyCode==33) { //PAGE_UP : image + 100
    if (imageId<MAX_IMAGE_ID-99)
      imageId+=100;
    reloadImage();
    loop();
  }
  if (keyCode==34) { //PAGE_DOWN : image - 100
    if (imageId>MIN_IMAGE_ID+99)
      imageId-=100;
    reloadImage();
    loop();
  }
  
  if (keyCode==ESC) {
    exit();
  }
}

interface Drawable {

  public void prepare();
  public void draw();
}
/**
Questa classe rappresenta una spezzata, che pu\u00f2 essere chiusa e in caso diventa un poligono.
*/
class PointList {
  //fields
  ArrayList<PVector> points = new ArrayList<PVector>();
  boolean closed = false;
  
  //methods
  
  /**
  Ritorna l'ennesimo punto.
  */
  public PVector get(int n) {
    return points.get(n);
  }
  
  /**
  Ritorna il numero di punti nell'elenco
  */
  public int size() {
    return points.size();
  }
  
  /**
  Resetta questa PointList.
  */
  public void reset() {
    points = new ArrayList<PVector>();
    closed = false;
  }
  
  /**
  Aggiunge un punto alla lista.
  */
  public void add(float x,float y) {
    if (!closed) {
      PVector v = new PVector(x,y);
      points.add(v);
    } else {
      throw new IllegalStateException("PointList was already closed.");
    }
  } 
  
  /**
  Controlla se questa polilinea \u00e8 chiusa.
  */
  public boolean isClosed() {
    return closed;
  }
  
  public void markAsClosed() {
    closed = true;
  }
  
  /**
  Chiude la selezione utilizzando il punto iniziale.
  */
  public void close() {
    if (!closed) {
      PVector start = get(0);
      add(start.x,start.y);
      closed = true;
    }
  }
  
  /**
  Normalizza le coordinate dei punti.
  */
  public void normalize(int xMaxCurrent,int yMaxCurrent,int xMaxNew,int yMaxNew) {
    double xFactor = (double)(xMaxNew)/(double)(xMaxCurrent);
    double yFactor = (double)(yMaxNew)/(double)(yMaxCurrent);
    
    ArrayList<PVector> newList = new ArrayList<PVector>();
    for (int i=0;i<size();i++) {
      PVector p = get(i);
      newList.add(new PVector((float)((float)(p.x)*xFactor),(float)((float)(p.y)*yFactor)));
    }
    points = newList;
  }
    
  /**
  Carica i dati da JSON
  */
  public void fromJson(JSONArray array) {
    points = new ArrayList<PVector>();
    for (int i=0;i<array.size();i++) {
      JSONObject obj = array.getJSONObject(i);
      add(obj.getFloat("x"),obj.getFloat("y"));
    }
  }
  
  /**
  Salva i dati in JSON
  */
  public JSONArray toJson() {
    JSONArray array = new JSONArray();
    for (int i=0;i<points.size();i++) {
      PVector v = points.get(i);
      JSONObject obj = new JSONObject();
      obj.setFloat("x",v.x);
      obj.setFloat("y",v.y);
      array.setJSONObject(i,obj);
    }
    return array;
  }
  
  /**
  Disegna la polilinea
  */
  public void draw() {
    beginShape();
    for (int i=0;i<points.size();i++) {
      PVector p0 = points.get(i);
      vertex(p0.x,p0.y);
    }
    endShape();
  }
}
/**
Questa classe contiene i dati relativi a un'unica area taggata.
Contiene l'elenco dei punti e il tipo di tag (stringa).
*/

class TaggedArea implements Drawable {

  int COLOR_UNTAGGED = color(0,0,0);
  int COLOR_ALLUMINIO = color(184,184,184);
  int COLOR_PLASTICA = color(224,222,83);
  int COLOR_VETRO = color(39,205,0);
  int COLOR_CARCAR = color(228,234,234);
  int COLOR_INDIFFERENZIATA = color(229,72,223);
  int COLOR_INGOMBRANTI = color(213,67,67);
  int COLOR_ORGANICO = color(121,67,67);
  
  
  PointList myArea = new PointList();
  String tagType = "untagged";
  
  public PointList getPointList() {
    return myArea;
  }
  
  public String getTagType() {
    return tagType;
  }
  
  public void setTagType(String type) {
    if (getPointList().isClosed()) {
      tagType = type;
      logInfo("Area taggata come : "+type);
    } else {
      logError("Errore : \u00c8 necessario chiudere l'area per effettuare il tag.");
    }
  }
  
  public void fromJson(JSONObject data) {
    tagType = data.getString("type");
    myArea.fromJson(data.getJSONArray("area"));
    myArea.markAsClosed();
  }
  
  public JSONObject toJson() {
    JSONObject data = new JSONObject();
    data.setString("type",tagType);
    data.setJSONArray("area",myArea.toJson());
    return data;
  }
  
  public void prepare() {
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
  
  public void draw() {
    noFill();
    getPointList().draw();
    print("polyline drawed\n");
  }
    
}

/**
Questa classe contiene i dati relativi ad una singola immagine taggata.
Contiene pi\u00f9 tagged area. I tag vengono normalizzati prima del salvataggio e dopo il caricamento.
*/

class TaggedImage implements Drawable {

    String imagePath;
    int imageWidth;
    int imageHeight;

    ArrayList<TaggedArea> taggedAreas = new ArrayList<TaggedArea>();
  
    public int size() {
      return taggedAreas.size();
    }
    
    public void checkNotEmpty() {
      if (taggedAreas.size()==0) {
        taggedAreas.add(new TaggedArea());
      }
    }
    
    public TaggedArea getLastTaggedArea() {
      checkNotEmpty();
      return taggedAreas.get(taggedAreas.size()-1);
    }
    
    public void deleteLastTaggedArea() {
      taggedAreas.remove(taggedAreas.size()-1);
      checkNotEmpty();
    }
    
    public void newTaggedArea() {
      taggedAreas.add(new TaggedArea());
    }
  
    public String getImagePath() {
      return imagePath;
    }
    
    public void setImagePath(String imageP) {
      imagePath = imageP;
    }
  
    public int getImageWidth() {
      return imageWidth;
    }
    
    public int getImageHeight() {
      return imageHeight;
    }

    public void setupWithImage(PImage img) {
      imageWidth = img.width;
      imageHeight = img.height;
    }
    
    public void fromJson(JSONObject obj) {
      imagePath = obj.getString("imagePath");
      imageWidth = obj.getInt("imageWidth");
      imageHeight = obj.getInt("imageHeight");
      JSONArray areas = obj.getJSONArray("areas");
      for (int i=0;i<areas.size();i++) {
        TaggedArea a = new TaggedArea();
        a.fromJson(areas.getJSONObject(i));
        taggedAreas.add(a);
      }
    }
    
    public void afterLoad(int stageWidth,int stageHeight) {
      for (int i=0;i<taggedAreas.size();i++) {
        taggedAreas.get(i).getPointList().normalize(imageWidth,imageHeight,stageWidth,stageHeight);
      }
    }
    
    public JSONObject toJson() {
      JSONArray areas = new JSONArray();
      for (int i=0;i<taggedAreas.size();i++) {
        TaggedArea ta = taggedAreas.get(i);
        areas.setJSONObject(i,ta.toJson());
      }
      JSONObject data = new JSONObject();
      data.setJSONArray("areas",areas);
      data.setString("imagePath",imagePath);
      data.setInt("imageWidth",imageWidth);
      data.setInt("imageHeight",imageHeight);
      return data;
    }
    
    public void beforeSave(int stageWidth,int stageHeight) {
      for (int i=0;i<taggedAreas.size();i++) {
        taggedAreas.get(i).getPointList().normalize(stageWidth,stageHeight,imageWidth,imageHeight);
      }
    }
    
    public void prepare() {
      stroke(2);
      color(255);
    }
    
    public void draw() {
      for (int i=0;i<taggedAreas.size();i++) {
        TaggedArea ta = taggedAreas.get(i);
        ta.prepare();
        ta.draw();
      }
      
    }

}
  public void settings() {  size(800,630); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "trash" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
