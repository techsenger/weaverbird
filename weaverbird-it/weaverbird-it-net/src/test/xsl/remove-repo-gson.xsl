<?xml version="1.0" encoding="UTF-8"?>
<!--
    In this module the tests run in the boot layer, which already contains Gson (it is used by net-shared). The repo
    component is created above the boot layer, so its own Gson would give automatic modules two modules with the same
    name. The template removes Gson from the repo component configuration. Production configuration is not affected.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="no"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="Module[@groupId='com.google.code.gson' and @artifactId='gson']"/>
</xsl:stylesheet>
