<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="tei"
  xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns="http://www.tei-c.org/ns/1.0" version="1.0">

  <!-- possibly improved version of movingnotes.xsl, which I found at
    http://wiki.tei-c.org/index.php/NotesToRefs.xsl  — Syd Bauman, 2011-04-09
    -->

  <!-- Minor modifications by Philip R. Burns.  2012/09/14.
       Only move notes which are descendants of <text>.
    -->

  <!-- just to make it easy to change the type= of our generated <ref> elements -->

  <xsl:variable name="refType" select="'nota'"/>

  <!-- identity transform -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

  <!-- if <text> doesn't have a <back>, give it one ... -->
  <xsl:template match="tei:text">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:if test="not( tei:back )">
        <back>
          <!-- ... inserting our note <div> into it -->
          <xsl:call-template name="make-note-div"/>
        </back>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

  <!-- insert our note <div> at the end of source <back> -->
  <xsl:template match="back">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:call-template name="make-note-div"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template name="make-note-div">
    <div type="notes">
      <head xml:lang="en">Notes</head>
      <pb n="nts"/>
      <xsl:apply-templates select="//tei:text//tei:note" mode="moveback"/>
    </div>
  </xsl:template>

  <!-- When we come across a <note> in normal processing, put out a <ref> -->
  <!-- instead. (We use <ref> instead of <ptr> as that's what PhiloLogic -->
  <!-- calls for, even though <ptr> would be more appropriate.) -->
  <xsl:template match="tei:text//tei:note">
    <ptr type="{$refType}" target="{concat('n',generate-id(.))}" xmlns="http://www.tei-c.org/ns/1.0">
      <xsl:attribute name="xml:id">
        <!-- see [1] -->
        <xsl:value-of select="concat('r',generate-id(.))"/>
      </xsl:attribute>
      <xsl:attribute name="n">
        <xsl:number/>
      </xsl:attribute>
    </ptr>
  </xsl:template>

  <!-- When we call for a <note> in move-me-to-back mode, put out a -->
  <!-- copy of the context node (the <note>) with the needed extra -->
  <!-- attributes. -->
  <xsl:template match="tei:text//tei:note" mode="moveback">
    <note place="foot" target="{concat('r',generate-id(.))}">
      <!-- see [1] -->
      <xsl:attribute name="xml:id">
        <xsl:value-of select="concat('n',generate-id(.))"/>
      </xsl:attribute>
      <!-- copy source attrs, but don't overwrite the ones we just -->
      <!-- created -->
      <xsl:copy-of select="@*[not(attribute::xml:id or attribute::target or attribute::place)]"/>
      <xsl:apply-templates select="node()"/>
    </note>
    <!-- add a carriage return, for tidiness -->
    <xsl:text>
</xsl:text>
  </xsl:template>

  <!-- [1]
    You can't create the xml:id= attribute using an attribute value template
    (at least not using xsltproc). I.e.,
       xml:id={concat('n',generate-id(.))}
    results in an error. I *think* this is because the result of evaluating an
    attribute value template is always a string, but an NCName is required. When
    the XSLT engine builds a tree fragment (as with <xsl:attribute>), it can
    build something more complex than a string, in this case an NCName.
  -->
</xsl:stylesheet>
