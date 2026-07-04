<?xml version="1.0" encoding="UTF-8"?>
<!-- Feuille de style XSLT : transformation du document meteo en histogramme SVG animé.
     Un histogramme est dessiné pour chaque mesure ; les barres « poussent »
     depuis l'axe horizontal grâce aux éléments <animate> (animation SMIL). -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/2000/svg">

  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

  <!-- Paramètres de dessin -->
  <xsl:variable name="largeurBarre" select="50"/>
  <xsl:variable name="pas"          select="75"/>   <!-- barre + espacement -->
  <xsl:variable name="echelle"      select="10"/>   <!-- pixels par °C -->
  <xsl:variable name="hausseur"     select="420"/>  <!-- hauteur d'un histogramme -->
  <xsl:variable name="base"         select="360"/>  <!-- ordonnée de l'axe des abscisses -->
  <xsl:variable name="margeGauche"  select="60"/>

  <xsl:template match="/meteo">
    <svg xmlns="http://www.w3.org/2000/svg"
         width="{$margeGauche + count(mesure[1]/ville) * $pas + 40}"
         height="{count(mesure) * $hausseur}">

      <xsl:apply-templates select="mesure"/>
    </svg>
  </xsl:template>

  <xsl:template match="mesure">
    <!-- Chaque mesure est décalée verticalement -->
    <g transform="translate(0, {(position() - 1) * $hausseur})">

      <!-- Titre de l'histogramme -->
      <text x="{$margeGauche}" y="35" font-family="Arial" font-size="22"
            font-weight="bold" fill="#1a4a7a">
        Températures du <xsl:value-of select="@date"/>
      </text>

      <!-- Axe vertical et axe horizontal -->
      <line x1="{$margeGauche - 10}" y1="60" x2="{$margeGauche - 10}" y2="{$base}"
            stroke="#333" stroke-width="2"/>
      <line x1="{$margeGauche - 10}" y1="{$base}"
            x2="{$margeGauche + count(ville) * $pas}" y2="{$base}"
            stroke="#333" stroke-width="2"/>

      <!-- Graduations tous les 10 °C -->
      <xsl:call-template name="graduation"><xsl:with-param name="t" select="10"/></xsl:call-template>
      <xsl:call-template name="graduation"><xsl:with-param name="t" select="20"/></xsl:call-template>
      <xsl:call-template name="graduation"><xsl:with-param name="t" select="30"/></xsl:call-template>

      <xsl:apply-templates select="ville"/>
    </g>
  </xsl:template>

  <xsl:template name="graduation">
    <xsl:param name="t"/>
    <line x1="{$margeGauche - 15}" y1="{$base - $t * $echelle}"
          x2="{$margeGauche - 5}"  y2="{$base - $t * $echelle}"
          stroke="#333" stroke-width="2"/>
    <text x="{$margeGauche - 20}" y="{$base - $t * $echelle + 5}"
          font-family="Arial" font-size="13" text-anchor="end" fill="#333">
      <xsl:value-of select="$t"/>
    </text>
  </xsl:template>

  <xsl:template match="ville">
    <xsl:variable name="h" select="@temperature * $echelle"/>
    <xsl:variable name="x" select="$margeGauche + (position() - 1) * $pas"/>

    <!-- Barre animée : la hauteur croît de 0 à sa valeur finale -->
    <rect x="{$x}" width="{$largeurBarre}" y="{$base - $h}" height="{$h}" rx="4">
      <xsl:attribute name="fill">
        <xsl:choose>
          <xsl:when test="@temperature &gt;= 25">#e74c3c</xsl:when>
          <xsl:when test="@temperature &lt;= 19">#3498db</xsl:when>
          <xsl:otherwise>#f39c12</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <animate attributeName="height" from="0" to="{$h}"
               dur="2s" begin="0s" fill="freeze"/>
      <animate attributeName="y" from="{$base}" to="{$base - $h}"
               dur="2s" begin="0s" fill="freeze"/>
      <!-- Légère pulsation continue pour un effet « vivant » -->
      <animate attributeName="opacity" values="1;0.75;1"
               dur="3s" begin="2s" repeatCount="indefinite"/>
    </rect>

    <!-- Valeur de la température au-dessus de la barre (apparaît après l'animation) -->
    <text x="{$x + $largeurBarre div 2}" y="{$base - $h - 8}"
          font-family="Arial" font-size="15" font-weight="bold"
          text-anchor="middle" fill="#333" opacity="0">
      <xsl:value-of select="@temperature"/>°
      <animate attributeName="opacity" from="0" to="1"
               dur="0.5s" begin="2s" fill="freeze"/>
    </text>

    <!-- Nom de la ville sous l'axe -->
    <text x="{$x + $largeurBarre div 2}" y="{$base + 20}"
          font-family="Arial" font-size="13" text-anchor="middle" fill="#333">
      <xsl:value-of select="@nom"/>
    </text>
  </xsl:template>

</xsl:stylesheet>
