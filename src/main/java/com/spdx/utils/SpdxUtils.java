package com.spdx.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.spdx.enums.HeaderEnums;
import com.spdx.enums.SpdxFieldEnums;
import com.spdx.model.HeaderFileNumber;
import com.spdx.model.SpdxComponent;
import com.spdx.model.SpdxLicense;
import com.spdx.model.SpdxRelease;

/**
 * Processing SpdxFile Version 2.1
 * @author CuongVT
 *
 */
public class SpdxUtils {
	
	final static Logger logger = Logger.getLogger(SpdxUtils.class);
	
	public void importComponentAddReLease(String pathFile, SpdxComponent component, SpdxRelease release) {
		Set<String> creators = new HashSet<String>();
		if (pathFile.isEmpty() || pathFile == "")
			return;
		else {
			File fileImport = new File(pathFile);
			try {
				Scanner scanner = new Scanner(fileImport, "UTF-8");
				boolean hasSPDXIDDocument = false;
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.contains(SpdxFieldEnums.SPDXVersion.value())) {
						release.setSpdxVersion(line.substring(SpdxFieldEnums.SPDXVersion.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.DataLicense.value())) {
						release.setDataLicense(line.substring(SpdxFieldEnums.DataLicense.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.SPDXID.value()) && !hasSPDXIDDocument) {
						release.setSpdxId(line.substring(SpdxFieldEnums.SPDXID.value().length()).trim());
						hasSPDXIDDocument = true;
					} else if (line.contains(SpdxFieldEnums.SPDXID.value()) && hasSPDXIDDocument) {
						release.setPackageSpdxId(line.substring(SpdxFieldEnums.SPDXID.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.DocumentName.value())) {
						release.setDocumentName(line.substring(SpdxFieldEnums.DocumentName.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.DocumentNamespace.value())) {
						release.setDocumentNamespace(line.substring(SpdxFieldEnums.DocumentNamespace.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.Created.value())) {
						release.setCreated(line.substring(SpdxFieldEnums.Created.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageName.value())) {
						component.setName(line.substring(SpdxFieldEnums.PackageName.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageVersion.value())) {
						release.setPackageVersion(line.substring(SpdxFieldEnums.PackageVersion.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageDownloadLocation.value())) {
						release.setPackageDownloadLocation(line.substring(SpdxFieldEnums.PackageDownloadLocation.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageVerificationCode.value())) {
						release.setPackageVerificationCode(line.substring(SpdxFieldEnums.PackageVerificationCode.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageLicenseConcluded.value())) {
						component.setPackageLicenseConcluded(line.substring(SpdxFieldEnums.PackageLicenseConcluded.value().length()).trim());
						release.setPackageLicenseConcluded(line.substring(SpdxFieldEnums.PackageLicenseConcluded.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageLicenseInfoFromFiles.value())) {
						release.setPackageLicenseInfoFromFiles(line.substring(SpdxFieldEnums.PackageLicenseInfoFromFiles.value().length()).trim());
					} else if (line.contains(SpdxFieldEnums.PackageLicenseDeclared.value())) {
						release.setPackageLicenseDeclared(line.substring(SpdxFieldEnums.PackageLicenseDeclared.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageCopyrightText.value())) {
						release.setPackageCopyrightText(line.substring(SpdxFieldEnums.PackageCopyrightText.value().length()).trim());
						break;
					} else if(line.contains(SpdxFieldEnums.PackageFileName.value())) {
						release.setPackageFileName(line.substring(SpdxFieldEnums.PackageFileName.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageFileName.value())) {
						release.setPackageFileName(line.substring(SpdxFieldEnums.PackageFileName.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.FilesAnalyzed.value())) {
						release.setFilesAnalyzed(line.substring(SpdxFieldEnums.FilesAnalyzed.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.PackageHomePage.value())) {
						component.setHomepage(line.substring(SpdxFieldEnums.PackageHomePage.value().length()).trim());
						release.setPackageHomePage(line.substring(SpdxFieldEnums.PackageHomePage.value().length()).trim());
					} else if(line.contains(SpdxFieldEnums.Creator.value())) {
						creators.add(line.substring(SpdxFieldEnums.Creator.value().length()).trim());
					}
				}
				
				release.setPackageLicenseComments(getPackageLicenseComments(pathFile));
				release.setCreators(creators);
				setPackageComment(pathFile, release);
				
				scanner.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<SpdxLicense> importLicense(String pathFile, int sizeOfFile, int lineNumOfLicenseInfor) throws Exception {
		List<SpdxLicense> lstLicense = new ArrayList<SpdxLicense>();
		if (pathFile.isEmpty() || pathFile == "")
			return new ArrayList<SpdxLicense>();
		else {
			File fileImport = new File(pathFile);
			try {
				Scanner scanner = new Scanner(fileImport, "UTF-8");
				int lineNum = 0;
				scanLicense(lstLicense, lineNum, scanner, sizeOfFile, lineNumOfLicenseInfor);
				scanner.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lstLicense.stream()
				.filter(item -> item.getShortName() != null && item.getFullName() != null
						&& !item.getShortName().isEmpty() && !item.getFullName().isEmpty())
				.collect(Collectors.toList());
	}
	
	
	private void scanLicense(List<SpdxLicense> lstSpdx, int lineNumber, Scanner scanner, int lineSize,
			int lineNumOfLicenseInfor) {
		SpdxLicense dto = new SpdxLicense();
		StringBuffer extractText = new StringBuffer();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			lineNumber++;
			if (lineNumber <= lineNumOfLicenseInfor)
				continue;
			if (line.contains(SpdxFieldEnums.LicenseID.value())) {
				dto.setShortName(line.substring(SpdxFieldEnums.LicenseID.value().length()).trim());
			} else if (line.contains(SpdxFieldEnums.LicenseName.value())) {
				dto.setFullName(line.substring(SpdxFieldEnums.LicenseName.value().length()).trim());
			} else if (line.contains(SpdxFieldEnums.ExtractedText.value()) && !line.contains("</text>")) {
				extractText.append(
						line.substring(SpdxFieldEnums.ExtractedText.value().length()).replace("<text>", "").trim());
			} else if (line.contains(SpdxFieldEnums.ExtractedText.value()) && line.contains("</text>")) {
				extractText.append(
						line.substring(SpdxFieldEnums.ExtractedText.value().length()).replace("</text>", "").trim());
				break;
			} else if (line.contains("</text>") && !line.contains(SpdxFieldEnums.ExtractedText.value())) {
				extractText.append("\n" + line);
				break;
			} else {
				extractText.append("\n" + line);
			}
		}
		if (dto != null) {
			dto.setText(extractText.toString().replace("</text>", "").trim());
			lstSpdx.add(dto);
		}
		if (lineNumber < (lineSize - 1))
			scanLicense(lstSpdx, lineNumber, scanner, lineSize, lineNumOfLicenseInfor);
	}

	private String getPackageLicenseComments(String pathFile) throws Exception {
		File file = new File(pathFile);
		int lineNumberOfPackageLicenseComments = getLinumberOfPackageLicenseComments(pathFile);
		StringBuffer packageLicenseComments = new StringBuffer();
		Scanner scanner = new Scanner(file, "UTF-8");
		int count = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			count++;
			if (count < lineNumberOfPackageLicenseComments)
				continue;
			if (line.contains(SpdxFieldEnums.PackageLicenseComments.value())) {
				packageLicenseComments
						.append(line.substring(SpdxFieldEnums.PackageLicenseComments.value().length()).trim());
			} else if (line.contains(SpdxFieldEnums.PackageLicenseInfoFromFiles.value())) {
				break;
			} else
				packageLicenseComments.append("\n" + line);
		}
		scanner.close();

		return packageLicenseComments.toString().replace("</text>", "").trim();
	}

	private void setPackageComment(String pathFile, SpdxRelease dto) throws IOException {
		File file = new File(pathFile);
		// Get line number of package comment in spdx file
		int startLineNumberOfPackageComment = 0;
		int endLineNumberOfPackageComment = 0;
		
		Scanner scanner = new Scanner(file, "UTF-8");
		
		boolean isStart = false;
		int count = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			count++;
			if (line.contains(SpdxFieldEnums.PackageComment.value())) {
				startLineNumberOfPackageComment = count;
				isStart = true;
			}
			
			if (isStart && line.contains("</text>")) {
				endLineNumberOfPackageComment = count;
				break;
			}
		}
		scanner.close();
		
		
		if (isStart && startLineNumberOfPackageComment < endLineNumberOfPackageComment) {
			// Get content package comment
			String modificationRecordTmp = (String) FileUtils.readLines(file).get(startLineNumberOfPackageComment);
			dto.setModificationRecord(modificationRecordTmp.split(":")[1].trim());
			
			String complieOptionsTmp = (String) FileUtils.readLines(file).get(startLineNumberOfPackageComment + 1);
			dto.setCompileOptions(complieOptionsTmp.split(":")[1].trim());
			
			Map<String, String> packageCommentExtends = new HashMap<String, String>();
			for (int i = startLineNumberOfPackageComment + 2; i < endLineNumberOfPackageComment - 1; i++) {
				
				String packageCommentExtendTmp = (String) FileUtils.readLines(file).get(i);
				String[] packageCommentExtend = packageCommentExtendTmp.split(":");
				packageCommentExtends.put(packageCommentExtend[0].trim(), packageCommentExtend[1].trim());
			}
			
			dto.setPackageCommentExtends(packageCommentExtends);
		}
	}
	
	public int getLinumberOfPackageLicenseComments(String pathFile) throws Exception {
		File file = new File(pathFile);
		Scanner scanner = new Scanner(file, "UTF-8");
		int count = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			count++;
			if (line.contains(SpdxFieldEnums.PackageLicenseComments.value())) {
				break;
			}
		}
		scanner.close();
		return count;
	}

	public HeaderFileNumber getHeaderLineIndex(String pathFile) throws Exception {
		HeaderFileNumber headers = new HeaderFileNumber();
		File file = new File(pathFile);
		Scanner scanner = new Scanner(file, "UTF-8");
		int count = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			count++;
			if (line.contains(SpdxFieldEnums.PackageDownloadLocation.value()))
				headers.setDownLoadUrl(line.substring(SpdxFieldEnums.PackageDownloadLocation.value().length()).trim());
			if (line.contains(HeaderEnums.FileInformation.value()))
				headers.setFileInformation(count + 2);
			if (line.contains(HeaderEnums.LicenseInformation.value()))
				headers.setLicenseInformation(count + 2);
			if (line.contains(SpdxFieldEnums.PackageLicenseComments.value()))
				headers.setPackageLicenseComments(count);
		}
		scanner.close();
		if(headers.getLicenseInformation() == 0)
			headers.setLicenseInformation(count);
		headers.setSizeOfFile(count);
		return headers;
	}
}
