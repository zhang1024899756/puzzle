package com.game.utils;

import android.graphics.Bitmap;


import java.util.ArrayList;
import java.util.List;

/**
 * 切图类
 */

public class ImageSplitter {

    public static List<ImagePiece> splitImage(Bitmap bitmap,int piece){

        List<ImagePiece> imagePieces = new ArrayList<>();
        //拿到图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //计算切片宽高（正方形）
        int pieceWidth = Math.min(width,height) / piece;

        for (int i = 0;i < piece; i++ )
        {
            for (int j = 0;j < piece; j++ )
            {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * piece);

                int x = j * pieceWidth;
                int y = i * pieceWidth;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,pieceWidth,pieceWidth));

                imagePieces.add(imagePiece);

            }
        }

        return imagePieces;
    };

}
