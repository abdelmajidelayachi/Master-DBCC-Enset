<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:template match="/releve">
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Relevé de compte</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        h1 { color: #2E5C8A; }
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #999; padding: 8px; text-align: center; }
        th { background-color: #2E5C8A; color: white; }
        .CREDIT { color: green; font-weight: bold; }
        .DEBIT { color: red; font-weight: bold; }
        .totaux { margin-top: 20px; font-size: 1.1em; }
    </style>
</head>
<body>
    <h1>Relevé de compte n° <xsl:value-of select="@RIB"/></h1>
    <p><b>Date du relevé : </b><xsl:value-of select="dateReleve"/></p>
    <p><b>Solde : </b><xsl:value-of select="solde"/> DH</p>
    <p><b>Période : </b>du <xsl:value-of select="operations/@dateDebut"/>
       au <xsl:value-of select="operations/@dateFin"/></p>

    <table>
        <tr>
            <th>Date</th>
            <th>Type</th>
            <th>Description</th>
            <th>Montant</th>
        </tr>
        <xsl:for-each select="operations/operation">
            <tr>
                <td><xsl:value-of select="@date"/></td>
                <td class="{@type}"><xsl:value-of select="@type"/></td>
                <td><xsl:value-of select="@description"/></td>
                <td><xsl:value-of select="@montant"/> DH</td>
            </tr>
        </xsl:for-each>
    </table>

    <div class="totaux">
        <p class="CREDIT">Total des crédits :
            <xsl:value-of select="sum(operations/operation[@type='CREDIT']/@montant)"/> DH
        </p>
        <p class="DEBIT">Total des débits :
            <xsl:value-of select="sum(operations/operation[@type='DEBIT']/@montant)"/> DH
        </p>
    </div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
