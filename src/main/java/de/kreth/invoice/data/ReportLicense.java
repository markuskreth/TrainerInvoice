package de.kreth.invoice.data;

public enum ReportLicense {

    TRAINER("/reports/mtv_gross_buchholz_trainer.jrxml"),
    ASSISTANT("/reports/mtv_gross_buchholz.jrxml");

    private String ressource;

    private ReportLicense(String ressource) {
	this.ressource = ressource;
    }

    public String getRessource() {
	return ressource;
    }

    public String getLabel() {
	if (TRAINER == this) {
	    return "Trainer";
	}
	return "Übungsleiter";
    }
}
