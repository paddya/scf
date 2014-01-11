/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scf.util;



/**
 *
 * @author paddya
 */
public class StringUtil
{

    /**
     * Method to join array elements of type string
     *
     * @author Hendrik Will, imwill.com
     * @param inputArray Array which contains strings
     * @param glueString String between each array element
     * @return String containing all array elements seperated by glue string
     */
    public static String implodeArray(String[] inputArray, String glueString)
    {

        /**
         * Output variable
         */
        String output = "";

        if (inputArray.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(inputArray[0]);

            for (int i = 1; i < inputArray.length; i++) {
                sb.append(glueString);
                sb.append(inputArray[i]);
            }

            output = sb.toString();
        }

        return output;
    }



    public static String getStringBoardString(String[][] stringBoard)
    {
        String[] rows = new String[stringBoard.length];

        for (int row = 0; row < stringBoard.length; row++) {
            rows[row] = "[" + StringUtil.implodeArray(stringBoard[row], ",") + ",]";
        }

        return "[" + StringUtil.implodeArray(rows, ",") + ",]";
    }
}


