<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:template match="/releve">
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Opérations de crédit</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        h1 { color: #2E5C8A; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #999; padding: 8px; text-align: center; }
        th { background-color: #2E7D32; color: white; }
        td { color: green; font-weight: bold; }
    </style>
</head>
<body>
    <h1>Opérations de type CREDIT - Relevé <xsl:value-of select="@RIB"/></h1>
    <table>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Montant</th>
        </tr>
        <xsl:for-each select="operations/operation[@type='CREDIT']">
            <tr>
                <td><xsl:value-of select="@date"/></td>
                <td><xsl:value-of select="@description"/></td>
                <td><xsl:value-of select="@montant"/> DH</td>
            </tr>
        </xsl:for-each>
    </table>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
