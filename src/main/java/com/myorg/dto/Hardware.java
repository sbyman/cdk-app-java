package com.myorg.dto;

import java.util.Map;

public class Hardware {
    private String name;
    private String ram;
    private String rom;
    private Display display;
    private Map<String, String> optionalInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public Map<String, String> getOptionalInfo() {
        return optionalInfo;
    }

    public void setOptionalInfo(Map<String, String> optionalInfo) {
        this.optionalInfo = optionalInfo;
    }

    @Override
    public String toString() {
        return "Hardware{" +
                "nome='" + name + '\'' +
                ", ram='" + ram + '\'' +
                ", rom='" + rom + '\'' +
                ", display=" + display +
                ", optionalInfo=" + optionalInfo +
                '}';
    }
}
