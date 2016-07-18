/*
 * Copyright (c) 2000 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package mf.org.w3c.dom.css;

import mf.org.w3c.dom.DOMException;

/**
 * The <code>CSS2Properties</code> interface represents a convenience
 * mechanism for retrieving and setting properties within a
 * <code>CSSStyleDeclaration</code>. The attributes of this interface
 * correspond to all the properties specified in CSS2. Getting an attribute
 * of this interface is equivalent to calling the
 * <code>getPropertyValue</code> method of the
 * <code>CSSStyleDeclaration</code> interface. Setting an attribute of this
 * interface is equivalent to calling the <code>setProperty</code> method of
 * the <code>CSSStyleDeclaration</code> interface.
 * <p> A conformant implementation of the CSS module is not required to
 * implement the <code>CSS2Properties</code> interface. If an implementation
 * does implement this interface, the expectation is that language-specific
 * methods can be used to cast from an instance of the
 * <code>CSSStyleDeclaration</code> interface to the
 * <code>CSS2Properties</code> interface.
 * <p> If an implementation does implement this interface, it is expected to
 * understand the specific syntax of the shorthand properties, and apply
 * their semantics; when the <code>margin</code> property is set, for
 * example, the <code>marginTop</code>, <code>marginRight</code>,
 * <code>marginBottom</code> and <code>marginLeft</code> properties are
 * actually being set by the underlying implementation.
 * <p> When dealing with CSS "shorthand" properties, the shorthand properties
 * should be decomposed into their component longhand properties as
 * appropriate, and when querying for their value, the form returned should
 * be the shortest form exactly equivalent to the declarations made in the
 * ruleset. However, if there is no shorthand declaration that could be
 * added to the ruleset without changing in any way the rules already
 * declared in the ruleset (i.e., by adding longhand rules that were
 * previously not declared in the ruleset), then the empty string should be
 * returned for the shorthand property.
 * <p> For example, querying for the <code>font</code> property should not
 * return "normal normal normal 14pt/normal Arial, sans-serif", when "14pt
 * Arial, sans-serif" suffices. (The normals are initial values, and are
 * implied by use of the longhand property.)
 * <p> If the values for all the longhand properties that compose a particular
 * string are the initial values, then a string consisting of all the
 * initial values should be returned (e.g. a <code>border-width</code> value
 * of "medium" should be returned as such, not as "").
 * <p> For some shorthand properties that take missing values from other
 * sides, such as the <code>margin</code>, <code>padding</code>, and
 * <code>border-[width|style|color]</code> properties, the minimum number of
 * sides possible should be used; i.e., "0px 10px" will be returned instead
 * of "0px 10px 0px 10px".
 * <p> If the value of a shorthand property can not be decomposed into its
 * component longhand properties, as is the case for the <code>font</code>
 * property with a value of "menu", querying for the values of the component
 * longhand properties should return the empty string.
 * <p>See also the <a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>Document Object Model (DOM) Level 2 Style Specification</a>.
 *
 * @since DOM Level 2
 */
public interface CSS2Properties {
    /**
     * See the azimuth property definition in CSS2.
     */
    String getAzimuth();

    /**
     * See the azimuth property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setAzimuth(String azimuth)
            throws DOMException;

    /**
     * See the background property definition in CSS2.
     */
    String getBackground();

    /**
     * See the background property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBackground(String background)
            throws DOMException;

    /**
     * See the background-attachment property definition in CSS2.
     */
    String getBackgroundAttachment();

    /**
     * See the background-attachment property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBackgroundAttachment(String backgroundAttachment)
            throws DOMException;

    /**
     * See the background-color property definition in CSS2.
     */
    String getBackgroundColor();

    /**
     * See the background-color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBackgroundColor(String backgroundColor)
            throws DOMException;

    /**
     * See the background-image property definition in CSS2.
     */
    String getBackgroundImage();

    /**
     * See the background-image property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBackgroundImage(String backgroundImage)
            throws DOMException;

    /**
     * See the background-position property definition in CSS2.
     */
    String getBackgroundPosition();

    /**
     * See the background-position property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBackgroundPosition(String backgroundPosition)
            throws DOMException;

    /**
     * See the background-repeat property definition in CSS2.
     */
    String getBackgroundRepeat();

    /**
     * See the background-repeat property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBackgroundRepeat(String backgroundRepeat)
            throws DOMException;

    /**
     * See the border property definition in CSS2.
     */
    String getBorder();

    /**
     * See the border property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorder(String border)
            throws DOMException;

    /**
     * See the border-collapse property definition in CSS2.
     */
    String getBorderCollapse();

    /**
     * See the border-collapse property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderCollapse(String borderCollapse)
            throws DOMException;

    /**
     * See the border-color property definition in CSS2.
     */
    String getBorderColor();

    /**
     * See the border-color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderColor(String borderColor)
            throws DOMException;

    /**
     * See the border-spacing property definition in CSS2.
     */
    String getBorderSpacing();

    /**
     * See the border-spacing property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderSpacing(String borderSpacing)
            throws DOMException;

    /**
     * See the border-style property definition in CSS2.
     */
    String getBorderStyle();

    /**
     * See the border-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderStyle(String borderStyle)
            throws DOMException;

    /**
     * See the border-top property definition in CSS2.
     */
    String getBorderTop();

    /**
     * See the border-top property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderTop(String borderTop)
            throws DOMException;

    /**
     * See the border-right property definition in CSS2.
     */
    String getBorderRight();

    /**
     * See the border-right property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderRight(String borderRight)
            throws DOMException;

    /**
     * See the border-bottom property definition in CSS2.
     */
    String getBorderBottom();

    /**
     * See the border-bottom property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderBottom(String borderBottom)
            throws DOMException;

    /**
     * See the border-left property definition in CSS2.
     */
    String getBorderLeft();

    /**
     * See the border-left property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderLeft(String borderLeft)
            throws DOMException;

    /**
     * See the border-top-color property definition in CSS2.
     */
    String getBorderTopColor();

    /**
     * See the border-top-color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderTopColor(String borderTopColor)
            throws DOMException;

    /**
     * See the border-right-color property definition in CSS2.
     */
    String getBorderRightColor();

    /**
     * See the border-right-color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderRightColor(String borderRightColor)
            throws DOMException;

    /**
     * See the border-bottom-color property definition in CSS2.
     */
    String getBorderBottomColor();

    /**
     * See the border-bottom-color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderBottomColor(String borderBottomColor)
            throws DOMException;

    /**
     * See the border-left-color property definition in CSS2.
     */
    String getBorderLeftColor();

    /**
     * See the border-left-color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderLeftColor(String borderLeftColor)
            throws DOMException;

    /**
     * See the border-top-style property definition in CSS2.
     */
    String getBorderTopStyle();

    /**
     * See the border-top-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderTopStyle(String borderTopStyle)
            throws DOMException;

    /**
     * See the border-right-style property definition in CSS2.
     */
    String getBorderRightStyle();

    /**
     * See the border-right-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderRightStyle(String borderRightStyle)
            throws DOMException;

    /**
     * See the border-bottom-style property definition in CSS2.
     */
    String getBorderBottomStyle();

    /**
     * See the border-bottom-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderBottomStyle(String borderBottomStyle)
            throws DOMException;

    /**
     * See the border-left-style property definition in CSS2.
     */
    String getBorderLeftStyle();

    /**
     * See the border-left-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderLeftStyle(String borderLeftStyle)
            throws DOMException;

    /**
     * See the border-top-width property definition in CSS2.
     */
    String getBorderTopWidth();

    /**
     * See the border-top-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderTopWidth(String borderTopWidth)
            throws DOMException;

    /**
     * See the border-right-width property definition in CSS2.
     */
    String getBorderRightWidth();

    /**
     * See the border-right-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderRightWidth(String borderRightWidth)
            throws DOMException;

    /**
     * See the border-bottom-width property definition in CSS2.
     */
    String getBorderBottomWidth();

    /**
     * See the border-bottom-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderBottomWidth(String borderBottomWidth)
            throws DOMException;

    /**
     * See the border-left-width property definition in CSS2.
     */
    String getBorderLeftWidth();

    /**
     * See the border-left-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderLeftWidth(String borderLeftWidth)
            throws DOMException;

    /**
     * See the border-width property definition in CSS2.
     */
    String getBorderWidth();

    /**
     * See the border-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBorderWidth(String borderWidth)
            throws DOMException;

    /**
     * See the bottom property definition in CSS2.
     */
    String getBottom();

    /**
     * See the bottom property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setBottom(String bottom)
            throws DOMException;

    /**
     * See the caption-side property definition in CSS2.
     */
    String getCaptionSide();

    /**
     * See the caption-side property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCaptionSide(String captionSide)
            throws DOMException;

    /**
     * See the clear property definition in CSS2.
     */
    String getClear();

    /**
     * See the clear property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setClear(String clear)
            throws DOMException;

    /**
     * See the clip property definition in CSS2.
     */
    String getClip();

    /**
     * See the clip property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setClip(String clip)
            throws DOMException;

    /**
     * See the color property definition in CSS2.
     */
    String getColor();

    /**
     * See the color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setColor(String color)
            throws DOMException;

    /**
     * See the content property definition in CSS2.
     */
    String getContent();

    /**
     * See the content property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setContent(String content)
            throws DOMException;

    /**
     * See the counter-increment property definition in CSS2.
     */
    String getCounterIncrement();

    /**
     * See the counter-increment property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCounterIncrement(String counterIncrement)
            throws DOMException;

    /**
     * See the counter-reset property definition in CSS2.
     */
    String getCounterReset();

    /**
     * See the counter-reset property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCounterReset(String counterReset)
            throws DOMException;

    /**
     * See the cue property definition in CSS2.
     */
    String getCue();

    /**
     * See the cue property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCue(String cue)
            throws DOMException;

    /**
     * See the cue-after property definition in CSS2.
     */
    String getCueAfter();

    /**
     * See the cue-after property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCueAfter(String cueAfter)
            throws DOMException;

    /**
     * See the cue-before property definition in CSS2.
     */
    String getCueBefore();

    /**
     * See the cue-before property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCueBefore(String cueBefore)
            throws DOMException;

    /**
     * See the cursor property definition in CSS2.
     */
    String getCursor();

    /**
     * See the cursor property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCursor(String cursor)
            throws DOMException;

    /**
     * See the direction property definition in CSS2.
     */
    String getDirection();

    /**
     * See the direction property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setDirection(String direction)
            throws DOMException;

    /**
     * See the display property definition in CSS2.
     */
    String getDisplay();

    /**
     * See the display property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setDisplay(String display)
            throws DOMException;

    /**
     * See the elevation property definition in CSS2.
     */
    String getElevation();

    /**
     * See the elevation property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setElevation(String elevation)
            throws DOMException;

    /**
     * See the empty-cells property definition in CSS2.
     */
    String getEmptyCells();

    /**
     * See the empty-cells property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setEmptyCells(String emptyCells)
            throws DOMException;

    /**
     * See the float property definition in CSS2.
     */
    String getCssFloat();

    /**
     * See the float property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setCssFloat(String cssFloat)
            throws DOMException;

    /**
     * See the font property definition in CSS2.
     */
    String getFont();

    /**
     * See the font property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFont(String font)
            throws DOMException;

    /**
     * See the font-family property definition in CSS2.
     */
    String getFontFamily();

    /**
     * See the font-family property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFontFamily(String fontFamily)
            throws DOMException;

    /**
     * See the font-size property definition in CSS2.
     */
    String getFontSize();

    /**
     * See the font-size property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFontSize(String fontSize)
            throws DOMException;

    /**
     * See the font-size-adjust property definition in CSS2.
     */
    String getFontSizeAdjust();

    /**
     * See the font-size-adjust property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFontSizeAdjust(String fontSizeAdjust)
            throws DOMException;

    /**
     * See the font-stretch property definition in CSS2.
     */
    String getFontStretch();

    /**
     * See the font-stretch property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFontStretch(String fontStretch)
            throws DOMException;

    /**
     * See the font-style property definition in CSS2.
     */
    String getFontStyle();

    /**
     * See the font-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFontStyle(String fontStyle)
            throws DOMException;

    /**
     * See the font-variant property definition in CSS2.
     */
    String getFontVariant();

    /**
     * See the font-variant property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFontVariant(String fontVariant)
            throws DOMException;

    /**
     * See the font-weight property definition in CSS2.
     */
    String getFontWeight();

    /**
     * See the font-weight property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setFontWeight(String fontWeight)
            throws DOMException;

    /**
     * See the height property definition in CSS2.
     */
    String getHeight();

    /**
     * See the height property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setHeight(String height)
            throws DOMException;

    /**
     * See the left property definition in CSS2.
     */
    String getLeft();

    /**
     * See the left property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setLeft(String left)
            throws DOMException;

    /**
     * See the letter-spacing property definition in CSS2.
     */
    String getLetterSpacing();

    /**
     * See the letter-spacing property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setLetterSpacing(String letterSpacing)
            throws DOMException;

    /**
     * See the line-height property definition in CSS2.
     */
    String getLineHeight();

    /**
     * See the line-height property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setLineHeight(String lineHeight)
            throws DOMException;

    /**
     * See the list-style property definition in CSS2.
     */
    String getListStyle();

    /**
     * See the list-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setListStyle(String listStyle)
            throws DOMException;

    /**
     * See the list-style-image property definition in CSS2.
     */
    String getListStyleImage();

    /**
     * See the list-style-image property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setListStyleImage(String listStyleImage)
            throws DOMException;

    /**
     * See the list-style-position property definition in CSS2.
     */
    String getListStylePosition();

    /**
     * See the list-style-position property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setListStylePosition(String listStylePosition)
            throws DOMException;

    /**
     * See the list-style-type property definition in CSS2.
     */
    String getListStyleType();

    /**
     * See the list-style-type property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setListStyleType(String listStyleType)
            throws DOMException;

    /**
     * See the margin property definition in CSS2.
     */
    String getMargin();

    /**
     * See the margin property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMargin(String margin)
            throws DOMException;

    /**
     * See the margin-top property definition in CSS2.
     */
    String getMarginTop();

    /**
     * See the margin-top property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMarginTop(String marginTop)
            throws DOMException;

    /**
     * See the margin-right property definition in CSS2.
     */
    String getMarginRight();

    /**
     * See the margin-right property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMarginRight(String marginRight)
            throws DOMException;

    /**
     * See the margin-bottom property definition in CSS2.
     */
    String getMarginBottom();

    /**
     * See the margin-bottom property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMarginBottom(String marginBottom)
            throws DOMException;

    /**
     * See the margin-left property definition in CSS2.
     */
    String getMarginLeft();

    /**
     * See the margin-left property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMarginLeft(String marginLeft)
            throws DOMException;

    /**
     * See the marker-offset property definition in CSS2.
     */
    String getMarkerOffset();

    /**
     * See the marker-offset property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMarkerOffset(String markerOffset)
            throws DOMException;

    /**
     * See the marks property definition in CSS2.
     */
    String getMarks();

    /**
     * See the marks property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMarks(String marks)
            throws DOMException;

    /**
     * See the max-height property definition in CSS2.
     */
    String getMaxHeight();

    /**
     * See the max-height property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMaxHeight(String maxHeight)
            throws DOMException;

    /**
     * See the max-width property definition in CSS2.
     */
    String getMaxWidth();

    /**
     * See the max-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMaxWidth(String maxWidth)
            throws DOMException;

    /**
     * See the min-height property definition in CSS2.
     */
    String getMinHeight();

    /**
     * See the min-height property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMinHeight(String minHeight)
            throws DOMException;

    /**
     * See the min-width property definition in CSS2.
     */
    String getMinWidth();

    /**
     * See the min-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setMinWidth(String minWidth)
            throws DOMException;

    /**
     * See the orphans property definition in CSS2.
     */
    String getOrphans();

    /**
     * See the orphans property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setOrphans(String orphans)
            throws DOMException;

    /**
     * See the outline property definition in CSS2.
     */
    String getOutline();

    /**
     * See the outline property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setOutline(String outline)
            throws DOMException;

    /**
     * See the outline-color property definition in CSS2.
     */
    String getOutlineColor();

    /**
     * See the outline-color property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setOutlineColor(String outlineColor)
            throws DOMException;

    /**
     * See the outline-style property definition in CSS2.
     */
    String getOutlineStyle();

    /**
     * See the outline-style property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setOutlineStyle(String outlineStyle)
            throws DOMException;

    /**
     * See the outline-width property definition in CSS2.
     */
    String getOutlineWidth();

    /**
     * See the outline-width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setOutlineWidth(String outlineWidth)
            throws DOMException;

    /**
     * See the overflow property definition in CSS2.
     */
    String getOverflow();

    /**
     * See the overflow property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setOverflow(String overflow)
            throws DOMException;

    /**
     * See the padding property definition in CSS2.
     */
    String getPadding();

    /**
     * See the padding property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPadding(String padding)
            throws DOMException;

    /**
     * See the padding-top property definition in CSS2.
     */
    String getPaddingTop();

    /**
     * See the padding-top property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPaddingTop(String paddingTop)
            throws DOMException;

    /**
     * See the padding-right property definition in CSS2.
     */
    String getPaddingRight();

    /**
     * See the padding-right property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPaddingRight(String paddingRight)
            throws DOMException;

    /**
     * See the padding-bottom property definition in CSS2.
     */
    String getPaddingBottom();

    /**
     * See the padding-bottom property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPaddingBottom(String paddingBottom)
            throws DOMException;

    /**
     * See the padding-left property definition in CSS2.
     */
    String getPaddingLeft();

    /**
     * See the padding-left property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPaddingLeft(String paddingLeft)
            throws DOMException;

    /**
     * See the page property definition in CSS2.
     */
    String getPage();

    /**
     * See the page property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPage(String page)
            throws DOMException;

    /**
     * See the page-break-after property definition in CSS2.
     */
    String getPageBreakAfter();

    /**
     * See the page-break-after property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPageBreakAfter(String pageBreakAfter)
            throws DOMException;

    /**
     * See the page-break-before property definition in CSS2.
     */
    String getPageBreakBefore();

    /**
     * See the page-break-before property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPageBreakBefore(String pageBreakBefore)
            throws DOMException;

    /**
     * See the page-break-inside property definition in CSS2.
     */
    String getPageBreakInside();

    /**
     * See the page-break-inside property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPageBreakInside(String pageBreakInside)
            throws DOMException;

    /**
     * See the pause property definition in CSS2.
     */
    String getPause();

    /**
     * See the pause property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPause(String pause)
            throws DOMException;

    /**
     * See the pause-after property definition in CSS2.
     */
    String getPauseAfter();

    /**
     * See the pause-after property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPauseAfter(String pauseAfter)
            throws DOMException;

    /**
     * See the pause-before property definition in CSS2.
     */
    String getPauseBefore();

    /**
     * See the pause-before property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPauseBefore(String pauseBefore)
            throws DOMException;

    /**
     * See the pitch property definition in CSS2.
     */
    String getPitch();

    /**
     * See the pitch property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPitch(String pitch)
            throws DOMException;

    /**
     * See the pitch-range property definition in CSS2.
     */
    String getPitchRange();

    /**
     * See the pitch-range property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPitchRange(String pitchRange)
            throws DOMException;

    /**
     * See the play-during property definition in CSS2.
     */
    String getPlayDuring();

    /**
     * See the play-during property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPlayDuring(String playDuring)
            throws DOMException;

    /**
     * See the position property definition in CSS2.
     */
    String getPosition();

    /**
     * See the position property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setPosition(String position)
            throws DOMException;

    /**
     * See the quotes property definition in CSS2.
     */
    String getQuotes();

    /**
     * See the quotes property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setQuotes(String quotes)
            throws DOMException;

    /**
     * See the richness property definition in CSS2.
     */
    String getRichness();

    /**
     * See the richness property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setRichness(String richness)
            throws DOMException;

    /**
     * See the right property definition in CSS2.
     */
    String getRight();

    /**
     * See the right property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setRight(String right)
            throws DOMException;

    /**
     * See the size property definition in CSS2.
     */
    String getSize();

    /**
     * See the size property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setSize(String size)
            throws DOMException;

    /**
     * See the speak property definition in CSS2.
     */
    String getSpeak();

    /**
     * See the speak property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setSpeak(String speak)
            throws DOMException;

    /**
     * See the speak-header property definition in CSS2.
     */
    String getSpeakHeader();

    /**
     * See the speak-header property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setSpeakHeader(String speakHeader)
            throws DOMException;

    /**
     * See the speak-numeral property definition in CSS2.
     */
    String getSpeakNumeral();

    /**
     * See the speak-numeral property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setSpeakNumeral(String speakNumeral)
            throws DOMException;

    /**
     * See the speak-punctuation property definition in CSS2.
     */
    String getSpeakPunctuation();

    /**
     * See the speak-punctuation property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setSpeakPunctuation(String speakPunctuation)
            throws DOMException;

    /**
     * See the speech-rate property definition in CSS2.
     */
    String getSpeechRate();

    /**
     * See the speech-rate property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setSpeechRate(String speechRate)
            throws DOMException;

    /**
     * See the stress property definition in CSS2.
     */
    String getStress();

    /**
     * See the stress property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setStress(String stress)
            throws DOMException;

    /**
     * See the table-layout property definition in CSS2.
     */
    String getTableLayout();

    /**
     * See the table-layout property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setTableLayout(String tableLayout)
            throws DOMException;

    /**
     * See the text-align property definition in CSS2.
     */
    String getTextAlign();

    /**
     * See the text-align property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setTextAlign(String textAlign)
            throws DOMException;

    /**
     * See the text-decoration property definition in CSS2.
     */
    String getTextDecoration();

    /**
     * See the text-decoration property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setTextDecoration(String textDecoration)
            throws DOMException;

    /**
     * See the text-indent property definition in CSS2.
     */
    String getTextIndent();

    /**
     * See the text-indent property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setTextIndent(String textIndent)
            throws DOMException;

    /**
     * See the text-shadow property definition in CSS2.
     */
    String getTextShadow();

    /**
     * See the text-shadow property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setTextShadow(String textShadow)
            throws DOMException;

    /**
     * See the text-transform property definition in CSS2.
     */
    String getTextTransform();

    /**
     * See the text-transform property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setTextTransform(String textTransform)
            throws DOMException;

    /**
     * See the top property definition in CSS2.
     */
    String getTop();

    /**
     * See the top property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setTop(String top)
            throws DOMException;

    /**
     * See the unicode-bidi property definition in CSS2.
     */
    String getUnicodeBidi();

    /**
     * See the unicode-bidi property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setUnicodeBidi(String unicodeBidi)
            throws DOMException;

    /**
     * See the vertical-align property definition in CSS2.
     */
    String getVerticalAlign();

    /**
     * See the vertical-align property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setVerticalAlign(String verticalAlign)
            throws DOMException;

    /**
     * See the visibility property definition in CSS2.
     */
    String getVisibility();

    /**
     * See the visibility property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setVisibility(String visibility)
            throws DOMException;

    /**
     * See the voice-family property definition in CSS2.
     */
    String getVoiceFamily();

    /**
     * See the voice-family property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setVoiceFamily(String voiceFamily)
            throws DOMException;

    /**
     * See the volume property definition in CSS2.
     */
    String getVolume();

    /**
     * See the volume property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setVolume(String volume)
            throws DOMException;

    /**
     * See the white-space property definition in CSS2.
     */
    String getWhiteSpace();

    /**
     * See the white-space property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setWhiteSpace(String whiteSpace)
            throws DOMException;

    /**
     * See the widows property definition in CSS2.
     */
    String getWidows();

    /**
     * See the widows property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setWidows(String widows)
            throws DOMException;

    /**
     * See the width property definition in CSS2.
     */
    String getWidth();

    /**
     * See the width property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setWidth(String width)
            throws DOMException;

    /**
     * See the word-spacing property definition in CSS2.
     */
    String getWordSpacing();

    /**
     * See the word-spacing property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setWordSpacing(String wordSpacing)
            throws DOMException;

    /**
     * See the z-index property definition in CSS2.
     */
    String getZIndex();

    /**
     * See the z-index property definition in CSS2.
     *
     * @throws DOMException SYNTAX_ERR: Raised if the new value has a syntax error and is
     *                      unparsable.
     *                      <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this property is readonly.
     */
    void setZIndex(String zIndex)
            throws DOMException;

}
