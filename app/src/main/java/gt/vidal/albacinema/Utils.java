package gt.vidal.albacinema;

/**
 * Created by alejandroalvarado on 16/09/16.
 */
public class Utils
{
    public static String firstCaptial(String s)
    {
        if (s == null || s.isEmpty())
            return s;
        if (s.length() < 2)
            return s.toUpperCase();

        StringBuilder b = new StringBuilder();
        int indexOfFirstNonSpace = -1, i = 0;
        for (char c : s.toCharArray())
        {
            if (Character.isWhitespace(c))
            {
                i++;
            }
            else
            {
                indexOfFirstNonSpace = i;
                break;
            }
        }

        if (indexOfFirstNonSpace < 0 || indexOfFirstNonSpace >= s.length())
            return  s;

        b.append(s.substring(0, indexOfFirstNonSpace + 1).toUpperCase());
        b.append(s.substring(indexOfFirstNonSpace + 1, s.length()).toLowerCase());
        return b.toString();
    }

}
