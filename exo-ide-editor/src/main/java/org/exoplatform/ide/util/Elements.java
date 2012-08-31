// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.exoplatform.ide.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import elemental.client.Browser;
import elemental.dom.Text;
import elemental.html.AnchorElement;
import elemental.html.BRElement;
import elemental.html.BodyElement;
import elemental.html.ButtonElement;
import elemental.html.CanvasElement;
import elemental.html.DivElement;
import elemental.html.Document;
import elemental.html.Element;
import elemental.html.FormElement;
import elemental.html.HeadElement;
import elemental.html.IFrameElement;
import elemental.html.ImageElement;
import elemental.html.InputElement;
import elemental.html.LIElement;
import elemental.html.ParagraphElement;
import elemental.html.PreElement;
import elemental.html.SpanElement;
import elemental.html.TableCellElement;
import elemental.html.TableElement;
import elemental.html.TableRowElement;
import elemental.html.TextAreaElement;
import elemental.html.UListElement;
import elemental.html.Window;
import elemental.js.html.JsElement;
import elemental.ranges.Range;

import org.exoplatform.ide.json.JsonArray;

/**
 * Simple utility class for shortening frequent calls to Elemental libraries.
 *
 */
public class Elements {
  
  /**
   * A regular expression used by the {@link #markup(Element, String, String)} function to search
   * for http links and new lines.
   */
  private static final RegExp REGEXP_MARKUP =
      RegExp.compile("(?:(?:(?:https?://)|(?:www\\.))[^\\s]+)|[\n]", "ig");

  @SuppressWarnings("unchecked")
  public static <T extends com.google.gwt.dom.client.Element, R extends Element> R asJsElement(
      T element) {
    return (R) element.<JsElement>cast();
  }

  public static AnchorElement createAnchorElement(String... classNames) {
    AnchorElement elem = getDocument().createAnchorElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static ButtonElement createButtonElement(String... classNames) {
    ButtonElement elem = getDocument().createButtonElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static BRElement createBRElement(String... classNames) {
    BRElement elem = getDocument().createBRElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static CanvasElement createCanvas(String... classNames) {
    CanvasElement elem = getDocument().createCanvasElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static DivElement createDivElement(String... classNames) {
    DivElement elem = getDocument().createDivElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static Element createElement(String tagName, String... classNames) {
    Element elem = getDocument().createElement(tagName);
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static FormElement createFormElement(String... classNames) {
    FormElement elem = getDocument().createFormElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static TableElement createTableElement(String... classNames) {
    TableElement elem = getDocument().createTableElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static TableRowElement createTRElement(String... classNames) {
    TableRowElement elem = getDocument().createTRElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static TableCellElement createTDElement(String... classNames) {
    TableCellElement elem = getDocument().createTDElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static InputElement createInputElement(String... classNames) {
    InputElement elem = getDocument().createInputElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static InputElement createInputTextElement(String... classNames) {
    InputElement elem = getDocument().createInputElement();
    addClassesToElement(elem, classNames);
    elem.setType("text");
    return elem;
  }

  public static IFrameElement createIFrameElement(String... classNames) {
    IFrameElement elem = getDocument().createIFrameElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static ImageElement createImageElement(String... classNames) {
    ImageElement elem = getDocument().createImageElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static SpanElement createSpanElement(String... classNames) {
    SpanElement elem = getDocument().createSpanElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static TextAreaElement createTextAreaElement(String... classNames) {
    TextAreaElement elem = getDocument().createTextAreaElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static ParagraphElement createParagraphElement(String... classNames) {
    ParagraphElement elem = getDocument().createParagraphElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static PreElement createPreElement(String... classNames) {
    PreElement elem = getDocument().createPreElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static LIElement createLiElement(String... classNames) {
    LIElement elem = getDocument().createLIElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static UListElement createUListElement(String... classNames) {
    UListElement elem = getDocument().createULElement();
    addClassesToElement(elem, classNames);
    return elem;
  }

  public static Text createTextNode(String data) {
    return getDocument().createTextNode(data);
  }

  public static Element getActiveElement() {
    return getDocument().getActiveElement();
  }

  public static Document getDocument() {
    return Browser.getDocument();
  }

  public static Window getWindow() {
    return Browser.getWindow();
  }

  public static BodyElement getBody() {
    return getBody(getDocument());
  }

  public static BodyElement getBody(Document document) {
    return document.getBody();
  }

  public static HeadElement getHead() {
    return getHead(getDocument());
  }

  public static HeadElement getHead(Document document) {
    HeadElement result = document.getHead();
    if (result == null) {
      // Some versions of Firefox return undefined for the document.head.
      result = (HeadElement) document.getElementsByTagName("head").item(0);
    }
    return result;
  }

  public static Element getElementById(String id) {
    return getDocument().getElementById(id);
  }

  public static void injectJs(String js) {
    Element scriptElem = createElement("script");
    scriptElem.setAttribute("language", "javascript");
    scriptElem.setTextContent(js);
    getBody().appendChild(scriptElem);
  }

  /**
   * Replaces the contents of an Element with the specified ID, with a single
   * element.
   *
   * @param id The ID of the Element that we will erase the contents of.
   * @param with The element that we will attach to the element that we just
   *        erased the contents of.
   */
  public static void replaceContents(String id, Element with) {
    Element parent = getElementById(id);
    replaceContents(parent, with);
  }

  /**
   * Replaces the contents of a container with the given contents.
   */
  public static void replaceContents(Element container, Element contents) {
    container.setInnerHTML("");
    container.appendChild(contents);
  }

  public static void setCollideTitle(String subtitle) {
    if (StringUtils.isNullOrEmpty(subtitle)) {
      getDocument().setTitle("Collide");
    } else {
      getDocument().setTitle(subtitle + " - Collide");
    }
  }
  
  /**
   * Scans a string converting any recognizable html links into an anchor tag and replacing newlines
   * with a &lt;br/&gt;. Once built the result is appended to the provided element.
   */
  // TODO: Long term we need a markdown engine :)
  public static void markup(Element e, String text, String linkCssClass) {
    e.setInnerHTML("");
    JsonArray<String> paragraphs = StringUtils.split(text, "\n\n");
    for (int i = 0; i < paragraphs.size(); i++) {
      markupParagraph(e, paragraphs.get(i), linkCssClass);
    }
  }

  /**
   * Creates a paragraph tag and fills it with spans and anchor tags internally.
   */
  private static void markupParagraph(Element parent, String text, String linkCssClass) {
    if (StringUtils.isNullOrWhitespace(text)) {
      // don't add any dom here
      return;
    }

    ParagraphElement myParagraph = createParagraphElement();
    int index = 0;
    REGEXP_MARKUP.setLastIndex(0);

    SpanElement current = createSpanElement();
    for (MatchResult match = REGEXP_MARKUP.exec(text); match != null;
        match = REGEXP_MARKUP.exec(text)) {
      current.setTextContent(text.substring(index, match.getIndex()));
      myParagraph.appendChild(current);
      current = createSpanElement();

      /*
       * If our match is a \n we need to create a <br/> element to force a line break, otherwise we
       * matched an http/www link so let's make an anchor tag out of it.
       */
      if (match.getGroup(0).equals("\n")) {
        myParagraph.appendChild(createBRElement());
      } else {
        AnchorElement anchor = createAnchorElement(linkCssClass);
        anchor.setHref(match.getGroup(0));
        anchor.setTarget("_blank");
        anchor.setTextContent(match.getGroup(0));
        myParagraph.appendChild(anchor);
      }

      index = match.getIndex() + match.getGroup(0).length();
    }
    current.setTextContent(text.substring(index));
    myParagraph.appendChild(current);
    parent.appendChild(myParagraph);
  }

  /**
   * Selects all text in the specified element.
   */
  public static void selectAllText(Element e) {
    Range range = Browser.getDocument().createRange();
    range.selectNode(e);
    Browser.getWindow().getSelection().addRange(range);
  }

  public static void addClassesToElement(Element e, String... classNames) {
    for (String className : classNames) {
      if (!StringUtils.isNullOrEmpty(className)) {
        e.addClassName(className);
      }
    }
  }

  private Elements() {
  } // COV_NF_LINE
}
