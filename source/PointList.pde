/**
Questa classe rappresenta una spezzata, che può essere chiusa e in caso diventa un poligono.
*/
class PointList {
  //fields
  ArrayList<PVector> points = new ArrayList<PVector>();
  boolean closed = false;
  
  //methods
  
  /**
  Ritorna l'ennesimo punto.
  */
  PVector get(int n) {
    return points.get(n);
  }
  
  /**
  Ritorna il numero di punti nell'elenco
  */
  int size() {
    return points.size();
  }
  
  /**
  Resetta questa PointList.
  */
  void reset() {
    points = new ArrayList<PVector>();
    closed = false;
  }
  
  /**
  Aggiunge un punto alla lista.
  */
  void add(float x,float y) {
    if (!closed) {
      PVector v = new PVector(x,y);
      points.add(v);
    } else {
      throw new IllegalStateException("PointList was already closed.");
    }
  } 
  
  /**
  Controlla se questa polilinea è chiusa.
  */
  boolean isClosed() {
    return closed;
  }
  
  void markAsClosed() {
    closed = true;
  }
  
  /**
  Chiude la selezione utilizzando il punto iniziale.
  */
  void close() {
    if (!closed) {
      PVector start = get(0);
      add(start.x,start.y);
      closed = true;
    }
  }
  
  /**
  Normalizza le coordinate dei punti.
  */
  void normalize(int xMaxCurrent,int yMaxCurrent,int xMaxNew,int yMaxNew) {
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
  void fromJson(JSONArray array) {
    points = new ArrayList<PVector>();
    for (int i=0;i<array.size();i++) {
      JSONObject obj = array.getJSONObject(i);
      add(obj.getFloat("x"),obj.getFloat("y"));
    }
  }
  
  /**
  Salva i dati in JSON
  */
  JSONArray toJson() {
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
  void draw() {
    beginShape();
    for (int i=0;i<points.size();i++) {
      PVector p0 = points.get(i);
      vertex(p0.x,p0.y);
    }
    endShape();
  }
}