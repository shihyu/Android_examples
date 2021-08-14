/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package weibo4j.http;

import java.util.HashMap;
import java.util.Map;

public class HTMLEntity {
    public static String escape(String original) {
        StringBuffer buf = new StringBuffer(original);
        escape(buf);
        return buf.toString();
    }

    public static void escape(StringBuffer original) {
        int index = 0;
        String escaped;
        while (index < original.length()) {
            escaped = entityEscapeMap.get(original.substring(index, index + 1));
            if (null != escaped) {
                original.replace(index, index + 1, escaped);
                index += escaped.length();
            } else {
                index++;
            }
        }
    }

    public static String unescape(String original) {
        StringBuffer buf = new StringBuffer(original);
        unescape(buf);
        return buf.toString();
    }

    public static void unescape(StringBuffer original) {
        int index = 0;
        int semicolonIndex = 0;
        String escaped;
        String entity;
        while (index < original.length()) {
            index = original.indexOf("&", index);
            if (-1 == index) {
                break;
            }
            semicolonIndex = original.indexOf(";", index);
            if (-1 != semicolonIndex && 10 > (semicolonIndex - index)) {
                escaped = original.substring(index, semicolonIndex + 1);
                entity = escapeEntityMap.get(escaped);
                if (null != entity) {
                    original.replace(index, semicolonIndex + 1, entity);
                }
                index++;
            } else {
                break;
            }
        }
    }

    private static Map<String, String> entityEscapeMap = new HashMap<String, String>();
    private static Map<String, String> escapeEntityMap = new HashMap<String, String>();

    static {
        String[][] entities =
                {{"&nbsp;", "&#160;"/* no-break space = non-breaking space */, "\u00A0"}
                        , {"&iexcl;", "&#161;"/* inverted exclamation mark */, "\u00A1"}
                        , {"&cent;", "&#162;"/* cent sign */, "\u00A2"}
                        , {"&pound;", "&#163;"/* pound sign */, "\u00A3"}
                        , {"&curren;", "&#164;"/* currency sign */, "\u00A4"}
                        , {"&yen;", "&#165;"/* yen sign = yuan sign */, "\u00A5"}
                        , {"&brvbar;", "&#166;"/* broken bar = broken vertical bar */, "\u00A6"}
                        , {"&sect;", "&#167;"/* section sign */, "\u00A7"}
                        , {"&uml;", "&#168;"/* diaeresis = spacing diaeresis */, "\u00A8"}
                        , {"&copy;", "&#169;"/* copyright sign */, "\u00A9"}
                        , {"&ordf;", "&#170;"/* feminine ordinal indicator */, "\u00AA"}
                        , {"&laquo;", "&#171;"/* left-pointing double angle quotation mark = left pointing guillemet */, "\u00AB"}
                        , {"&not;", "&#172;"/* not sign = discretionary hyphen */, "\u00AC"}
                        , {"&shy;", "&#173;"/* soft hyphen = discretionary hyphen */, "\u00AD"}
                        , {"&reg;", "&#174;"/* registered sign = registered trade mark sign */, "\u00AE"}
                        , {"&macr;", "&#175;"/* macron = spacing macron = overline = APL overbar */, "\u00AF"}
                        , {"&deg;", "&#176;"/* degree sign */, "\u00B0"}
                        , {"&plusmn;", "&#177;"/* plus-minus sign = plus-or-minus sign */, "\u00B1"}
                        , {"&sup2;", "&#178;"/* superscript two = superscript digit two = squared */, "\u00B2"}
                        , {"&sup3;", "&#179;"/* superscript three = superscript digit three = cubed */, "\u00B3"}
                        , {"&acute;", "&#180;"/* acute accent = spacing acute */, "\u00B4"}
                        , {"&micro;", "&#181;"/* micro sign */, "\u00B5"}
                        , {"&para;", "&#182;"/* pilcrow sign = paragraph sign */, "\u00B6"}
                        , {"&middot;", "&#183;"/* middle dot = Georgian comma = Greek middle dot */, "\u00B7"}
                        , {"&cedil;", "&#184;"/* cedilla = spacing cedilla */, "\u00B8"}
                        , {"&sup1;", "&#185;"/* superscript one = superscript digit one */, "\u00B9"}
                        , {"&ordm;", "&#186;"/* masculine ordinal indicator */, "\u00BA"}
                        , {"&raquo;", "&#187;"/* right-pointing double angle quotation mark = right pointing guillemet */, "\u00BB"}
                        , {"&frac14;", "&#188;"/* vulgar fraction one quarter = fraction one quarter */, "\u00BC"}
                        , {"&frac12;", "&#189;"/* vulgar fraction one half = fraction one half */, "\u00BD"}
                        , {"&frac34;", "&#190;"/* vulgar fraction three quarters = fraction three quarters */, "\u00BE"}
                        , {"&iquest;", "&#191;"/* inverted question mark = turned question mark */, "\u00BF"}
                        , {"&Agrave;", "&#192;"/* latin capital letter A with grave = latin capital letter A grave */, "\u00C0"}
                        , {"&Aacute;", "&#193;"/* latin capital letter A with acute */, "\u00C1"}
                        , {"&Acirc;", "&#194;"/* latin capital letter A with circumflex */, "\u00C2"}
                        , {"&Atilde;", "&#195;"/* latin capital letter A with tilde */, "\u00C3"}
                        , {"&Auml;", "&#196;"/* latin capital letter A with diaeresis */, "\u00C4"}
                        , {"&Aring;", "&#197;"/* latin capital letter A with ring above = latin capital letter A ring */, "\u00C5"}
                        , {"&AElig;", "&#198;"/* latin capital letter AE = latin capital ligature AE */, "\u00C6"}
                        , {"&Ccedil;", "&#199;"/* latin capital letter C with cedilla */, "\u00C7"}
                        , {"&Egrave;", "&#200;"/* latin capital letter E with grave */, "\u00C8"}
                        , {"&Eacute;", "&#201;"/* latin capital letter E with acute */, "\u00C9"}
                        , {"&Ecirc;", "&#202;"/* latin capital letter E with circumflex */, "\u00CA"}
                        , {"&Euml;", "&#203;"/* latin capital letter E with diaeresis */, "\u00CB"}
                        , {"&Igrave;", "&#204;"/* latin capital letter I with grave */, "\u00CC"}
                        , {"&Iacute;", "&#205;"/* latin capital letter I with acute */, "\u00CD"}
                        , {"&Icirc;", "&#206;"/* latin capital letter I with circumflex */, "\u00CE"}
                        , {"&Iuml;", "&#207;"/* latin capital letter I with diaeresis */, "\u00CF"}
                        , {"&ETH;", "&#208;"/* latin capital letter ETH */, "\u00D0"}
                        , {"&Ntilde;", "&#209;"/* latin capital letter N with tilde */, "\u00D1"}
                        , {"&Ograve;", "&#210;"/* latin capital letter O with grave */, "\u00D2"}
                        , {"&Oacute;", "&#211;"/* latin capital letter O with acute */, "\u00D3"}
                        , {"&Ocirc;", "&#212;"/* latin capital letter O with circumflex */, "\u00D4"}
                        , {"&Otilde;", "&#213;"/* latin capital letter O with tilde */, "\u00D5"}
                        , {"&Ouml;", "&#214;"/* latin capital letter O with diaeresis */, "\u00D6"}
                        , {"&times;", "&#215;"/* multiplication sign */, "\u00D7"}
                        , {"&Oslash;", "&#216;"/* latin capital letter O with stroke = latin capital letter O slash */, "\u00D8"}
                        , {"&Ugrave;", "&#217;"/* latin capital letter U with grave */, "\u00D9"}
                        , {"&Uacute;", "&#218;"/* latin capital letter U with acute */, "\u00DA"}
                        , {"&Ucirc;", "&#219;"/* latin capital letter U with circumflex */, "\u00DB"}
                        , {"&Uuml;", "&#220;"/* latin capital letter U with diaeresis */, "\u00DC"}
                        , {"&Yacute;", "&#221;"/* latin capital letter Y with acute */, "\u00DD"}
                        , {"&THORN;", "&#222;"/* latin capital letter THORN */, "\u00DE"}
                        , {"&szlig;", "&#223;"/* latin small letter sharp s = ess-zed */, "\u00DF"}
                        , {"&agrave;", "&#224;"/* latin small letter a with grave = latin small letter a grave */, "\u00E0"}
                        , {"&aacute;", "&#225;"/* latin small letter a with acute */, "\u00E1"}
                        , {"&acirc;", "&#226;"/* latin small letter a with circumflex */, "\u00E2"}
                        , {"&atilde;", "&#227;"/* latin small letter a with tilde */, "\u00E3"}
                        , {"&auml;", "&#228;"/* latin small letter a with diaeresis */, "\u00E4"}
                        , {"&aring;", "&#229;"/* latin small letter a with ring above = latin small letter a ring */, "\u00E5"}
                        , {"&aelig;", "&#230;"/* latin small letter ae = latin small ligature ae */, "\u00E6"}
                        , {"&ccedil;", "&#231;"/* latin small letter c with cedilla */, "\u00E7"}
                        , {"&egrave;", "&#232;"/* latin small letter e with grave */, "\u00E8"}
                        , {"&eacute;", "&#233;"/* latin small letter e with acute */, "\u00E9"}
                        , {"&ecirc;", "&#234;"/* latin small letter e with circumflex */, "\u00EA"}
                        , {"&euml;", "&#235;"/* latin small letter e with diaeresis */, "\u00EB"}
                        , {"&igrave;", "&#236;"/* latin small letter i with grave */, "\u00EC"}
                        , {"&iacute;", "&#237;"/* latin small letter i with acute */, "\u00ED"}
                        , {"&icirc;", "&#238;"/* latin small letter i with circumflex */, "\u00EE"}
                        , {"&iuml;", "&#239;"/* latin small letter i with diaeresis */, "\u00EF"}
                        , {"&eth;", "&#240;"/* latin small letter eth */, "\u00F0"}
                        , {"&ntilde;", "&#241;"/* latin small letter n with tilde */, "\u00F1"}
                        , {"&ograve;", "&#242;"/* latin small letter o with grave */, "\u00F2"}
                        , {"&oacute;", "&#243;"/* latin small letter o with acute */, "\u00F3"}
                        , {"&ocirc;", "&#244;"/* latin small letter o with circumflex */, "\u00F4"}
                        , {"&otilde;", "&#245;"/* latin small letter o with tilde */, "\u00F5"}
                        , {"&ouml;", "&#246;"/* latin small letter o with diaeresis */, "\u00F6"}
                        , {"&divide;", "&#247;"/* division sign */, "\u00F7"}
                        , {"&oslash;", "&#248;"/* latin small letter o with stroke = latin small letter o slash */, "\u00F8"}
                        , {"&ugrave;", "&#249;"/* latin small letter u with grave */, "\u00F9"}
                        , {"&uacute;", "&#250;"/* latin small letter u with acute */, "\u00FA"}
                        , {"&ucirc;", "&#251;"/* latin small letter u with circumflex */, "\u00FB"}
                        , {"&uuml;", "&#252;"/* latin small letter u with diaeresis */, "\u00FC"}
                        , {"&yacute;", "&#253;"/* latin small letter y with acute */, "\u00FD"}
                        , {"&thorn;", "&#254;"/* latin small letter thorn with */, "\u00FE"}
                        , {"&yuml;", "&#255;"/* latin small letter y with diaeresis */, "\u00FF"}
                        , {"&fnof;", "¡¼"/* latin small f with hook = function = florin */, "\u0192"}
/* Greek */
                        , {"&Alpha;", "£D"/* greek capital letter alpha */, "\u0391"}
                        , {"&Beta;", "£E"/* greek capital letter beta */, "\u0392"}
                        , {"&Gamma;", "£F"/* greek capital letter gamma */, "\u0393"}
                        , {"&Delta;", "£G"/* greek capital letter delta */, "\u0394"}
                        , {"&Epsilon;", "£H"/* greek capital letter epsilon */, "\u0395"}
                        , {"&Zeta;", "£I"/* greek capital letter zeta */, "\u0396"}
                        , {"&Eta;", "£J"/* greek capital letter eta */, "\u0397"}
                        , {"&Theta;", "£K"/* greek capital letter theta */, "\u0398"}
                        , {"&Iota;", "£L"/* greek capital letter iota */, "\u0399"}
                        , {"&Kappa;", "£M"/* greek capital letter kappa */, "\u039A"}
                        , {"&Lambda;", "£N"/* greek capital letter lambda */, "\u039B"}
                        , {"&Mu;", "£O"/* greek capital letter mu */, "\u039C"}
                        , {"&Nu;", "£P"/* greek capital letter nu */, "\u039D"}
                        , {"&Xi;", "£Q"/* greek capital letter xi */, "\u039E"}
                        , {"&Omicron;", "£R"/* greek capital letter omicron */, "\u039F"}
                        , {"&Pi;", "£S"/* greek capital letter pi */, "\u03A0"}
                        , {"&Rho;", "£T"/* greek capital letter rho */, "\u03A1"}
/* there is no Sigmaf and no \u03A2 */
                        , {"&Sigma;", "£U"/* greek capital letter sigma */, "\u03A3"}
                        , {"&Tau;", "£V"/* greek capital letter tau */, "\u03A4"}
                        , {"&Upsilon;", "£W"/* greek capital letter upsilon */, "\u03A5"}
                        , {"&Phi;", "£X"/* greek capital letter phi */, "\u03A6"}
                        , {"&Chi;", "£Y"/* greek capital letter chi */, "\u03A7"}
                        , {"&Psi;", "£Z"/* greek capital letter psi */, "\u03A8"}
                        , {"&Omega;", "£["/* greek capital letter omega */, "\u03A9"}
                        , {"&alpha;", "£\"/* greek small letter alpha */, "\u03B1"}
                        , {"&beta;", "£]"/* greek small letter beta */, "\u03B2"}
                        , {"&gamma;", "£^"/* greek small letter gamma */, "\u03B3"}
                        , {"&delta;", "£_"/* greek small letter delta */, "\u03B4"}
                        , {"&epsilon;", "£`"/* greek small letter epsilon */, "\u03B5"}
                        , {"&zeta;", "£a"/* greek small letter zeta */, "\u03B6"}
                        , {"&eta;", "£b"/* greek small letter eta */, "\u03B7"}
                        , {"&theta;", "£c"/* greek small letter theta */, "\u03B8"}
                        , {"&iota;", "£d"/* greek small letter iota */, "\u03B9"}
                        , {"&kappa;", "£e"/* greek small letter kappa */, "\u03BA"}
                        , {"&lambda;", "£f"/* greek small letter lambda */, "\u03BB"}
                        , {"&mu;", "£g"/* greek small letter mu */, "\u03BC"}
                        , {"&nu;", "£h"/* greek small letter nu */, "\u03BD"}
                        , {"&xi;", "£i"/* greek small letter xi */, "\u03BE"}
                        , {"&omicron;", "£j"/* greek small letter omicron */, "\u03BF"}
                        , {"&pi;", "£k"/* greek small letter pi */, "\u03C0"}
                        , {"&rho;", "£l"/* greek small letter rho */, "\u03C1"}
                        , {"&sigmaf;", "¡¼"/* greek small letter final sigma */, "\u03C2"}
                        , {"&sigma;", "£m"/* greek small letter sigma */, "\u03C3"}
                        , {"&tau;", "£n"/* greek small letter tau */, "\u03C4"}
                        , {"&upsilon;", "£o"/* greek small letter upsilon */, "\u03C5"}
                        , {"&phi;", "£p"/* greek small letter phi */, "\u03C6"}
                        , {"&chi;", "£q"/* greek small letter chi */, "\u03C7"}
                        , {"&psi;", "£r"/* greek small letter psi */, "\u03C8"}
                        , {"&omega;", "£s"/* greek small letter omega */, "\u03C9"}
                        , {"&thetasym;", "¡¼"/* greek small letter theta symbol */, "\u03D1"}
                        , {"&upsih;", "¡¼"/* greek upsilon with hook symbol */, "\u03D2"}
                        , {"&piv;", "¡¼"/* greek pi symbol */, "\u03D6"}
/* General Punctuation */
                        , {"&bull;", "¡E"/* bullet = black small circle */, "\u2022"}
/* bullet is NOT the same as bullet operator  ,"\u2219*/
                        , {"&hellip;", "¡K"/* horizontal ellipsis = three dot leader */, "\u2026"}
                        , {"&prime;", "¡¬"/* prime = minutes = feet */, "\u2032"}
                        , {"&Prime;", "¡ª"/* double prime = seconds = inches */, "\u2033"}
                        , {"&oline;", "¡Ã"/* overline = spacing overscore */, "\u203E"}
                        , {"&frasl;", "¡¼"/* fraction slash */, "\u2044"}
/* Letterlike Symbols */
                        , {"&weierp;", "¡¼"/* script capital P = power set = Weierstrass p */, "\u2118"}
                        , {"&image;", "¡¼"/* blackletter capital I = imaginary part */, "\u2111"}
                        , {"&real;", "¡¼"/* blackletter capital R = real part symbol */, "\u211C"}
                        , {"&trade;", "¡¼"/* trade mark sign */, "\u2122"}
                        , {"&alefsym;", "¡¼"/* alef symbol = first transfinite cardinal */, "\u2135"}
/* alef symbol is NOT the same as hebrew letter alef  ,"\u05D0"}*/
/* Arrows */
                        , {"&larr;", "¡ö"/* leftwards arrow */, "\u2190"}
                        , {"&uarr;", "¡ô"/* upwards arrow */, "\u2191"}
                        , {"&rarr;", "¡÷"/* rightwards arrow */, "\u2192"}
                        , {"&darr;", "¡õ"/* downwards arrow */, "\u2193"}
                        , {"&harr;", "¡¼"/* left right arrow */, "\u2194"}
                        , {"&crarr;", "¡¼"/* downwards arrow with corner leftwards = carriage return */, "\u21B5"}
                        , {"&lArr;", "¡¼"/* leftwards double arrow */, "\u21D0"}
/* Unicode does not say that lArr is the same as the 'is implied by' arrow but also does not have any other character for that function. So ? lArr can be used for 'is implied by' as ISOtech suggests */
                        , {"&uArr;", "¡¼"/* upwards double arrow */, "\u21D1"}
                        , {"&rArr;", "¡¼"/* rightwards double arrow */, "\u21D2"}
/* Unicode does not say this is the 'implies' character but does not have another character with this function so ? rArr can be used for 'implies' as ISOtech suggests */
                        , {"&dArr;", "¡¼"/* downwards double arrow */, "\u21D3"}
                        , {"&hArr;", "¡¼"/* left right double arrow */, "\u21D4"}
/* Mathematical Operators */
                        , {"&forall;", "¡¼"/* for all */, "\u2200"}
                        , {"&part;", "¡¼"/* partial differential */, "\u2202"}
                        , {"&exist;", "¡¼"/* there exists */, "\u2203"}
                        , {"&empty;", "¡¼"/* empty set = null set = diameter */, "\u2205"}
                        , {"&nabla;", "¡¼"/* nabla = backward difference */, "\u2207"}
                        , {"&isin;", "¡¼"/* element of */, "\u2208"}
                        , {"&notin;", "¡¼"/* not an element of */, "\u2209"}
                        , {"&ni;", "¡¼"/* contains as member */, "\u220B"}
/* should there be a more memorable name than 'ni'? */
                        , {"&prod;", "£S"/* n-ary product = product sign */, "\u220F"}
/* prod is NOT the same character as ,"\u03A0"}*/
                        , {"&sum;", "£U"/* n-ary sumation */, "\u2211"}
/* sum is NOT the same character as ,"\u03A3"}*/
                        , {"&minus;", "¡¼"/* minus sign */, "\u2212"}
                        , {"&lowast;", "¡¼"/* asterisk operator */, "\u2217"}
                        , {"&radic;", "¡Ô"/* square root = radical sign */, "\u221A"}
                        , {"&prop;", "¡¼"/* proportional to */, "\u221D"}
                        , {"&infin;", "¡Û"/* infinity */, "\u221E"}
                        , {"&ang;", "¡ç"/* angle */, "\u2220"}
                        , {"&and;", "£N"/* logical and = wedge */, "\u2227"}
                        , {"&or;", "£¾"/* logical or = vee */, "\u2228"}
                        , {"&cap;", "¡ä"/* intersection = cap */, "\u2229"}
                        , {"&cup;", "¡å"/* union = cup */, "\u222A"}
                        , {"&int;", "¡ì"/* integral */, "\u222B"}
                        , {"&there4;", "¡ï"/* therefore */, "\u2234"}
                        , {"&sim;", "¡ã"/* tilde operator = varies with = similar to */, "\u223C"}
/* tilde operator is NOT the same character as the tilde  ,"\u007E"}*/
                        , {"&cong;", "¡¼"/* approximately equal to */, "\u2245"}
                        , {"&asymp;", "¡Ü"/* almost equal to = asymptotic to */, "\u2248"}
                        , {"&ne;", "¡Ú"/* not equal to */, "\u2260"}
                        , {"&equiv;", "¡Ý"/* identical to */, "\u2261"}
                        , {"&le;", "¡¼"/* less-than or equal to */, "\u2264"}
                        , {"&ge;", "¡¼"/* greater-than or equal to */, "\u2265"}
                        , {"&sub;", "¡¼"/* subset of */, "\u2282"}
                        , {"&sup;", "¡¼"/* superset of */, "\u2283"}
/* note that nsup  'not a superset of  ,"\u2283"}*/
                        , {"&sube;", "¡¼"/* subset of or equal to */, "\u2286"}
                        , {"&supe;", "¡¼"/* superset of or equal to */, "\u2287"}
                        , {"&oplus;", "¡ò"/* circled plus = direct sum */, "\u2295"}
                        , {"&otimes;", "¡¼"/* circled times = vector product */, "\u2297"}
                        , {"&perp;", "¡æ"/* up tack = orthogonal to = perpendicular */, "\u22A5"}
                        , {"&sdot;", "¡¼"/* dot operator */, "\u22C5"}
/* dot operator is NOT the same character as ,"\u00B7"}
/* Miscellaneous Technical */
                        , {"&lceil;", "¡¼"/* left ceiling = apl upstile */, "\u2308"}
                        , {"&rceil;", "¡¼"/* right ceiling */, "\u2309"}
                        , {"&lfloor;", "¡¼"/* left floor = apl downstile */, "\u230A"}
                        , {"&rfloor;", "¡¼"/* right floor */, "\u230B"}
                        , {"&lang;", "¡¼"/* left-pointing angle bracket = bra */, "\u2329"}
/* lang is NOT the same character as ,"\u003C"}*/
                        , {"&rang;", "¡¼"/* right-pointing angle bracket = ket */, "\u232A"}
/* rang is NOT the same character as ,"\u003E"}*/
/* Geometric Shapes */
                        , {"&loz;", "¡¼"/* lozenge */, "\u25CA"}
/* Miscellaneous Symbols */
                        , {"&spades;", "¡¼"/* black spade suit */, "\u2660"}
/* black here seems to mean filled as opposed to hollow */
                        , {"&clubs;", "¡¼"/* black club suit = shamrock */, "\u2663"}
                        , {"&hearts;", "¡¼"/* black heart suit = valentine */, "\u2665"}
                        , {"&diams;", "¡¼"/* black diamond suit */, "\u2666"}
                        , {"&quot;", "&#34;" /* quotation mark = APL quote */, "\""}
                        , {"&amp;", "&#38;" /* ampersand */, "\u0026"}
                        , {"&lt;", "&#60;" /* less-than sign */, "\u003C"}
                        , {"&gt;", "&#62;" /* greater-than sign */, "\u003E"}
/* Latin Extended-A */
                        , {"&OElig;", "¡¼" /* latin capital ligature OE */, "\u0152"}
                        , {"&oelig;", "¡¼" /* latin small ligature oe */, "\u0153"}
/* ligature is a misnomer  this is a separate character in some languages */
                        , {"&Scaron;", "¡¼" /* latin capital letter S with caron */, "\u0160"}
                        , {"&scaron;", "¡¼" /* latin small letter s with caron */, "\u0161"}
                        , {"&Yuml;", "¡¼" /* latin capital letter Y with diaeresis */, "\u0178"}
/* Spacing Modifier Letters */
                        , {"&circ;", "¡¼" /* modifier letter circumflex accent */, "\u02C6"}
                        , {"&tilde;", "¡¼" /* small tilde */, "\u02DC"}
/* General Punctuation */
                        , {"&ensp;", "¡¼"/* en space */, "\u2002"}
                        , {"&emsp;", "¡¼"/* em space */, "\u2003"}
                        , {"&thinsp;", "¡¼"/* thin space */, "\u2009"}
                        , {"&zwnj;", "¡¼"/* zero width non-joiner */, "\u200C"}
                        , {"&zwj;", "¡¼"/* zero width joiner */, "\u200D"}
                        , {"&lrm;", "¡¼"/* left-to-right mark */, "\u200E"}
                        , {"&rlm;", "¡¼"/* right-to-left mark */, "\u200F"}
                        , {"&ndash;", "¡V"/* en dash */, "\u2013"}
                        , {"&mdash;", "¡X"/* em dash */, "\u2014"}
                        , {"&lsquo;", "¡y"/* left single quotation mark */, "\u2018"}
                        , {"&rsquo;", "¡z"/* right single quotation mark */, "\u2019"}
                        , {"&sbquo;", "¡¼"/* single low-9 quotation mark */, "\u201A"}
                        , {"&ldquo;", "¡u"/* left double quotation mark */, "\u201C"}
                        , {"&rdquo;", "¡v"/* right double quotation mark */, "\u201D"}
                        , {"&bdquo;", "¡¼"/* double low-9 quotation mark */, "\u201E"}
                        , {"&dagger;", "¡¼"/* dagger */, "\u2020"}
                        , {"&Dagger;", "¡¼"/* double dagger */, "\u2021"}
                        , {"&permil;", "¡¼"/* per mille sign */, "\u2030"}
                        , {"&lsaquo;", "¡¼"/* single left-pointing angle quotation mark */, "\u2039"}
/* lsaquo is proposed but not yet ISO standardized */
                        , {"&rsaquo;", "¡¼"/* single right-pointing angle quotation mark */, "\u203A"}
/* rsaquo is proposed but not yet ISO standardized */
                        , {"&euro;", "€" /* euro sign */, "\u20AC"}};
        for (String[] entity : entities) {
            entityEscapeMap.put(entity[2], entity[0]);
            escapeEntityMap.put(entity[0], entity[2]);
            escapeEntityMap.put(entity[1], entity[2]);
        }
    }

}
