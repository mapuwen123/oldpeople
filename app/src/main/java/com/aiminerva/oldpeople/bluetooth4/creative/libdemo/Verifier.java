package com.aiminerva.oldpeople.bluetooth4.creative.libdemo;

import java.util.Vector;

/**
 * Created by Aaron on 2017/2/16.
 */

public class Verifier
{
    private static byte HEAD3 = -91;

    private static byte[] crc_table = { 0, 94, -68, -30, 97, 63,
            -35, -125, -62, -100, 126, 32, -93, -3,
            31, 65, -99, -61, 33, 127, -4, -94,
            64, 30, 95, 1, -29, -67, 62, 96,
            -126, -36, 35, 125, -97, -63, 66, 28,
            -2, -96, -31, -65, 93, 3, -128, -34,
            60, 98, -66, -32, 2, 92, -33, -127,
            99, 61, 124, 34, -64, -98, 29, 67,
            -95, -1, 70, 24, -6, -92, 39, 121,
            -101, -59, -124, -38, 56, 102, -27, -69,
            89, 7, -37, -123, 103, 57, -70, -28,
            6, 88, 25, 71, -91, -5, 120, 38,
            -60, -102, 101, 59, -39, -121, 4, 90,
            -72, -26, -89, -7, 27, 69, -58, -104,
            122, 36, -8, -90, 68, 26, -103, -57,
            37, 123, 58, 100, -122, -40, 91, 5,
            -25, -71, -116, -46, 48, 110, -19, -77,
            81, 15, 78, 16, -14, -84, 47, 113,
            -109, -51, 17, 79, -83, -13, 112, 46,
            -52, -110, -45, -115, 111, 49, -78, -20,
            14, 80, -81, -15, 19, 77, -50, -112,
            114, 44, 109, 51, -47, -113, 12, 82,
            -80, -18, 50, 108, -114, -48, 83, 13,
            -17, -79, -16, -82, 76, 18, -111, -49,
            45, 115, -54, -108, 118, 40, -85, -11,
            23, 73, 8, 86, -76, -22, 105, 55,
            -43, -117, 87, 9, -21, -75, 54, 104,
            -118, -44, -107, -53, 41, 119, -12, -86,
            72, 22, -23, -73, 85, 11, -120, -42,
            52, 106, 43, 117, -105, -55, 74, 20,
            -10, -88, 116, 42, -56, -106, 21, 75,
            -87, -9, -74, -24, 10, 84, -41, -119,
            107, 53 };

    public static int checkIntactCnt(Vector<Byte> buffer)
    {
        int cnt = 0;
        if (buffer.size() > 4) {
            int begin = 0;
            int tokenposi = 0;
            int len = 0;
            try {
                while (begin < buffer.size() - 1)
                {
                    if (((Byte)buffer.get(begin)).byteValue() == HEAD3) {
                        tokenposi = begin + 1;
                        if (tokenposi < buffer.size() - 2) {
                            len = ((Byte)buffer.get(tokenposi + 1)).byteValue() & 0xFF;
                            int CRCposi = -1;
                            CRCposi = tokenposi + 2 + len;
                            if (buffer.size() > CRCposi) {
                                try {
                                    if (checkCRC(buffer, begin, CRCposi)) {
                                        begin = CRCposi + 1;
                                        cnt++;
                                        continue;
                                    }

                                    buffer.remove(begin);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();

                                    buffer.remove(begin);
                                }
                            }
                            else
                                return cnt;
                        }
                        else {
                            return cnt;
                        }
                    } else {
                        buffer.remove(begin);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cnt;
    }

    public static boolean checkCRC(Vector<Byte> buffer, int begin, int end)
    {
        byte last_crc = 0; byte cur_crc = 0; byte crc = 0;
        try {
            for (int i = begin; i < end; i++) {
                last_crc = cur_crc;
                cur_crc = crc_table[((last_crc ^ ((Byte)buffer.get(i)).byteValue() & 0xFF) & 0xFF)];
            }
            crc = ((Byte)buffer.get(end)).byteValue();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
        return cur_crc == crc;
    }
}