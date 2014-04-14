package helpers;


public enum ImageSize {



    ORIGINAL(0),
    ROUTE_LIST_THUMB(160,120),

    USER_AVATAR_SMALL(16),
    USER_AVATAR_BIG(80);

    public final int height;
    public final int width;
    public String holderJsDims;


    ImageSize(int size) {
        this(size,size);
    }

    ImageSize(int width, int height) {
      this.width = width;
      this.height = height;

      this.holderJsDims = width+"x"+height;
    }


}
