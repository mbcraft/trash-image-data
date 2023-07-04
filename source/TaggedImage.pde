
/**
Questa classe contiene i dati relativi ad una singola immagine taggata.
Contiene più tagged area. I tag vengono normalizzati prima del salvataggio e dopo il caricamento.
*/

class TaggedImage implements Drawable {

    String imagePath;
    int imageWidth;
    int imageHeight;

    ArrayList<TaggedArea> taggedAreas = new ArrayList<TaggedArea>();
  
    int size() {
      return taggedAreas.size();
    }
    
    void checkNotEmpty() {
      if (taggedAreas.size()==0) {
        taggedAreas.add(new TaggedArea());
      }
    }
    
    TaggedArea getLastTaggedArea() {
      checkNotEmpty();
      return taggedAreas.get(taggedAreas.size()-1);
    }
    
    void deleteLastTaggedArea() {
      taggedAreas.remove(taggedAreas.size()-1);
      checkNotEmpty();
    }
    
    void newTaggedArea() {
      taggedAreas.add(new TaggedArea());
    }
  
    String getImagePath() {
      return imagePath;
    }
    
    void setImagePath(String imageP) {
      imagePath = imageP;
    }
  
    int getImageWidth() {
      return imageWidth;
    }
    
    int getImageHeight() {
      return imageHeight;
    }

    void setupWithImage(PImage img) {
      imageWidth = img.width;
      imageHeight = img.height;
    }
    
    void fromJson(JSONObject obj) {
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
    
    void afterLoad(int stageWidth,int stageHeight) {
      for (int i=0;i<taggedAreas.size();i++) {
        taggedAreas.get(i).getPointList().normalize(imageWidth,imageHeight,stageWidth,stageHeight);
      }
    }
    
    JSONObject toJson() {
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
    
    void beforeSave(int stageWidth,int stageHeight) {
      for (int i=0;i<taggedAreas.size();i++) {
        taggedAreas.get(i).getPointList().normalize(stageWidth,stageHeight,imageWidth,imageHeight);
      }
    }
    
    void prepare() {
      stroke(2);
      color(255);
    }
    
    void draw() {
      for (int i=0;i<taggedAreas.size();i++) {
        TaggedArea ta = taggedAreas.get(i);
        ta.prepare();
        ta.draw();
      }
      
    }

}