package org.xueliang.commons.util.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtils {

    /**
     * 获取指定节点的第一个 ${tagName} 父节点
     * @param element
     * @param tagName
     * @return
     */
    public static Element parent(Element element, String tagName) {
        if (element == null) {
            return null;
        }
        Element parent = element.parent();
        if (parent == null) {
            return null;
        }
        if (parent.tagName().toLowerCase().equals(tagName.toLowerCase())) {
            return parent;
        }
        return parent(parent, tagName);
    }
    
    /**
     * 获取每个节点的第一个 ${tagName} 父节点
     * @param elements
     * @param tagName
     * @return
     */
    public static Elements parents(Elements elements, String tagName) {
        Elements elements2 = new Elements();
        for (Element element : elements) {
            Element parent = parent(element, tagName);
            if (parent != null) {
                elements2.add(parent);
            }
        }
        return elements2;
    }
    
    /**
     * 实现jQuery.closest函数
     * 获得匹配选择器的第一个祖先元素，从当前元素开始沿 DOM 树向上。
     * @param element
     * @param selector
     * @return 匹配到的对象，如果没有，则返回 null
     */
    public static Element closest(Element element, String selector) {
        Element e = null;
        Elements parents = element.parents();
        Elements _parents = element.ownerDocument().select(selector);
        Elements parentsWithSelf = new Elements();
        parentsWithSelf.add(element);
        parentsWithSelf.addAll(parents);
        boolean isFound = false;
        for (int i = 0, len = parentsWithSelf.size(); i < len && !isFound; i++) {
            Element parent = parentsWithSelf.get(i);
            for (int j = 0, jlen = _parents.size(); j < jlen; j++) {
                Element _parent = _parents.get(j);
                if (_parent.cssSelector().equals(parent.cssSelector())) {
                    e = parent; // get√
                    isFound = true;
                    break;
                }
            }
        }
        return e;
    }
    
    /**
     * 将相对地址转为绝对地址
     * @param baseUri
     * @param uri
     * @return
     */
    public static String absUrl(String baseUri, String uri) {
        Document document = Jsoup.parse("<a id=\"link\" href=\"" + uri + "\"></a>");
        document.setBaseUri(baseUri);
        return document.getElementById("link").absUrl("href");
    }
}
