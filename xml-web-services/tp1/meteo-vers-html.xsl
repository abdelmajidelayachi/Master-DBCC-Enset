<?xml version="1.0" encoding="UTF-8"?>
<!-- Feuille de style XSLT : transformation du document meteo en document HTML -->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" encoding="UTF-8" indent="yes"/>

  <xsl:template match="/meteo">
    <html lang="fr">
      <head>
        <meta charset="UTF-8"/>
        <title>Mesures des températures</title>
        <style>
          body    { font-family: Arial, sans-serif; margin: 2em; background: #f5f7fa; }
          h1      { color: #1a4a7a; }
          h2      { color: #2a6aa5; margin-top: 1.5em; }
          table   { border-collapse: collapse; width: 60%; background: white; }
          th, td  { border: 1px solid #b0c4d8; padding: 8px 14px; text-align: left; }
          th      { background: #2a6aa5; color: white; }
          tr:nth-child(even) td { background: #eaf1f8; }
          .chaud  { color: #c0392b; font-weight: bold; }
          .froid  { color: #2471a3; font-weight: bold; }
        </style>
      </head>
      <body>
        <h1>Mesures des températures des villes</h1>
        <xsl:apply-templates select="mesure"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="mesure">
    <h2>Mesure du <xsl:value-of select="@date"/></h2>
    <table>
      <tr>
        <th>Ville</th>
        <th>Température (°C)</th>
      </tr>
      <xsl:apply-templates select="ville"/>
      <tr>
        <th>Température moyenne</th>
        <th>
          <xsl:value-of select="format-number(sum(ville/@temperature) div count(ville), '0.0')"/>
        </th>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="ville">
    <tr>
      <td><xsl:value-of select="@nom"/></td>
      <td>
        <xsl:attribute name="class">
          <xsl:choose>
            <xsl:when test="@temperature &gt;= 25">chaud</xsl:when>
            <xsl:when test="@temperature &lt;= 19">froid</xsl:when>
          </xsl:choose>
        </xsl:attribute>
        <xsl:value-of select="@temperature"/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
